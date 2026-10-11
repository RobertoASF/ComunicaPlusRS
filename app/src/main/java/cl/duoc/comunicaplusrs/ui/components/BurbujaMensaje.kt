package cl.duoc.comunicaplusrs.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.model.esEscrito
import cl.duoc.comunicaplusrs.utils.formatearHora

// Globo de la conversación, como en una app de mensajería:
// lo que dijo la otra persona a la izquierda y lo que escribió el usuario a la derecha.
@Composable
fun BurbujaMensaje(
    mensaje: Mensaje,
    modifier: Modifier = Modifier,
    onLeer: (() -> Unit)? = null,
    onEliminar: (() -> Unit)? = null
) {
    val escrito = mensaje.esEscrito
    val colores = MaterialTheme.colorScheme

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (escrito) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (escrito) colores.primaryContainer else colores.surfaceVariant,
            contentColor = if (escrito) colores.onPrimaryContainer else colores.onSurfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (escrito) 16.dp else 4.dp,
                bottomEnd = if (escrito) 4.dp else 16.dp
            ),
            modifier = Modifier.fillMaxWidth(0.88f)
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp, top = 10.dp, end = 4.dp, bottom = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = if (escrito) Icons.Filled.Keyboard else Icons.Filled.Hearing,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )
                    Text(
                        text = if (escrito) "Tú" else "La otra persona",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatearHora(mensaje.fecha),
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = mensaje.texto,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(top = 4.dp, end = 8.dp, bottom = 6.dp)
                )

                if (onLeer != null || onEliminar != null) {
                    Row(
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        if (onLeer != null) {
                            IconButton(onClick = onLeer) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Leer en voz alta"
                                )
                            }
                        }
                        if (onEliminar != null) {
                            IconButton(onClick = onEliminar) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Eliminar mensaje"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
