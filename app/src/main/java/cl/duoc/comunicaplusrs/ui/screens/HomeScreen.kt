package cl.duoc.comunicaplusrs.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.comunicaplusrs.data.PreferenciasLocales
import cl.duoc.comunicaplusrs.model.SECCION_INICIO
import cl.duoc.comunicaplusrs.model.Usuario
import cl.duoc.comunicaplusrs.navigation.Routes
import cl.duoc.comunicaplusrs.utils.armarSaludo
import java.util.Calendar

private data class OpcionMenu(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val ruta: String
)

private val opcionesMenu = listOf(
    OpcionMenu("Escribir", "Escribe y la app lo lee en voz alta", Icons.Filled.Keyboard, Routes.ESCRIBIR),
    OpcionMenu("Hablar", "Convierte lo que te dicen en texto", Icons.Filled.Mic, Routes.HABLAR),
    OpcionMenu("Historial", "Lo que te dijeron y lo que respondiste", Icons.Filled.History, Routes.HISTORIAL),
    OpcionMenu("Buscar dispositivo", "Ubica tus teléfonos en el mapa", Icons.Filled.MyLocation, Routes.BUSCAR_DISPOSITIVO),
    OpcionMenu("Mi perfil", "Datos y preferencias de accesibilidad", Icons.Filled.ManageAccounts, Routes.PERFIL),
    OpcionMenu("Ayuda", "Tutorial paso a paso de la app", Icons.AutoMirrored.Filled.HelpOutline, Routes.ayuda(SECCION_INICIO))
)

// Menú principal que se muestra después de iniciar sesión.
@Composable
fun HomeScreen(
    usuario: Usuario?,
    onAbrir: (String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    val preferenciasLocales = remember {
        PreferenciasLocales(context)
    }

    // La invitación al tutorial solo aparece si nunca se ha abierto en este teléfono.
    var mostrarInvitacion by remember {
        mutableStateOf(!preferenciasLocales.tutorialVisto)
    }

    // El saludo entra con una animación al abrir el menú.
    var saludoVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        saludoVisible = true
    }

    val hora = remember {
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(
            visible = saludoVisible,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -it / 2 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = armarSaludo(hora, usuario?.nombre),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "¿Qué quieres hacer hoy?",
                    textAlign = TextAlign.Center
                )
            }
        }

        AnimatedVisibility(
            visible = mostrarInvitacion,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            InvitacionTutorial(
                onVer = {
                    mostrarInvitacion = false
                    onAbrir(Routes.ayuda(SECCION_INICIO))
                },
                onCerrar = {
                    mostrarInvitacion = false
                    preferenciasLocales.tutorialVisto = true
                }
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // En pantallas anchas (tablet u horizontal) se muestran 3 columnas en vez de 2.
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val columnas = if (maxWidth > 600.dp) 3 else 2

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                opcionesMenu
                    .chunked(columnas)
                    .forEach { fila ->
                        // IntrinsicSize.Min deja todas las tarjetas de la fila con la misma altura.
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.height(IntrinsicSize.Min)
                        ) {
                            fila.forEach { opcion ->
                                TarjetaMenu(
                                    opcion = opcion,
                                    onClick = { onAbrir(opcion.ruta) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }
            }
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null
            )
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Text(
                text = "CERRAR SESIÓN"
            )
        }
    }
}

@Composable
private fun InvitacionTutorial(
    onVer: () -> Unit,
    onCerrar: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Lightbulb,
                contentDescription = null
            )
            Spacer(
                modifier = Modifier.width(12.dp)
            )
            Text(
                text = "¿Primera vez en la app? Revisa el tutorial para aprender a usar cada función.",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCerrar) {
                Text("Ahora no")
            }
            TextButton(onClick = onVer) {
                Text("Ver tutorial")
            }
        }
    }
}

@Composable
private fun TarjetaMenu(
    opcion: OpcionMenu,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.heightIn(min = 150.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = opcion.icono,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = opcion.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = opcion.descripcion,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
