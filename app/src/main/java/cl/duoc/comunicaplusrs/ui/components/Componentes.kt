package cl.duoc.comunicaplusrs.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.comunicaplusrs.utils.observarConexion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    onVolver: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }
    )
}

// Botón grande que muestra una ruedita mientras se espera a Firebase.
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    icono: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp
            )
        } else {
            if (icono != null) {
                Icon(
                    imageVector = icono,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = texto,
                fontSize = 16.sp
            )
        }
    }
}

// Los mensajes se muestran siempre en texto y con color, nunca solo con sonido.
@Composable
fun MensajeEstado(
    mensaje: String,
    esError: Boolean
) {
    if (mensaje.isBlank()) return

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = mensaje,
        color = if (esError) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite }
    )
}

// Franja roja arriba de la app cuando no hay internet (restricción del proyecto).
@Composable
fun AvisoSinConexion() {
    val context = LocalContext.current
    val conexion = remember { context.observarConexion() }
    val hayConexion by conexion.collectAsStateWithLifecycle(initialValue = true)

    AnimatedVisibility(visible = !hayConexion) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(12.dp)
                .semantics { liveRegion = LiveRegionMode.Assertive },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.WifiOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sin conexión a internet. Tus datos no se podrán guardar.",
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FilaTabla(
    celdas: List<String>,
    pesos: List<Float>,
    esEncabezado: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (esEncabezado) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // zip junta cada texto con el peso de su columna.
        celdas
            .zip(pesos)
            .forEach { (texto, peso) ->
                Text(
                    text = texto,
                    modifier = Modifier.weight(peso),
                    fontSize = 13.sp,
                    fontWeight = if (esEncabezado) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
                )
            }
    }
}
