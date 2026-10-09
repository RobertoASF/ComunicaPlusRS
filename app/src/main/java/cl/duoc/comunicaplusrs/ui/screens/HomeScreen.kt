package cl.duoc.comunicaplusrs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.comunicaplusrs.model.Usuario
import cl.duoc.comunicaplusrs.navigation.Routes

private data class OpcionMenu(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val ruta: String
)

private val opcionesMenu = listOf(
    OpcionMenu("Escribir", "Escribe y la app lo lee en voz alta", Icons.Filled.Keyboard, Routes.ESCRIBIR),
    OpcionMenu("Hablar", "Convierte lo que te dicen en texto", Icons.Filled.Mic, Routes.HABLAR),
    OpcionMenu("Buscar dispositivo", "Ubica tus teléfonos en el mapa", Icons.Filled.MyLocation, Routes.BUSCAR_DISPOSITIVO),
    OpcionMenu("Mi perfil", "Datos y preferencias de accesibilidad", Icons.Filled.ManageAccounts, Routes.PERFIL)
)

// Menú principal que se muestra después de iniciar sesión.
@Composable
fun HomeScreen(
    usuario: Usuario?,
    onAbrir: (String) -> Unit,
    onLogout: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = if (usuario?.nombre.isNullOrBlank()) {
                "Bienvenido a Comunica Plus RS"
            } else {
                "Hola, ${usuario?.nombre}"
            },
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

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // En pantallas anchas (tablet u horizontal) se muestran 4 columnas en vez de 2.
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val columnas = if (maxWidth > 600.dp) 4 else 2

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                opcionesMenu
                    .chunked(columnas)
                    .forEach { fila ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            fila.forEach { opcion ->
                                TarjetaMenu(
                                    opcion = opcion,
                                    onClick = { onAbrir(opcion.ruta) },
                                    modifier = Modifier.weight(1f)
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
