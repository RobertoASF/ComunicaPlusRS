package cl.duoc.comunicaplusrs.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Estos componentes estaban dentro de RegisterScreen. Se movieron aquí
// porque ahora también se usan en la pantalla Perfil.

val TIPOS_COMUNICACION = listOf(
    "Texto",
    "Lengua de señas",
    "Texto y lengua de señas"
)

val PREFERENCIAS_INTERFAZ = listOf(
    "Estándar",
    "Texto grande"
)

val OPCIONES_ACCESIBILIDAD = listOf(
    "Texto grande",
    "Alto contraste",
    "Alertas visuales",
    "Vibración"
)

@Composable
fun TituloSeccion(texto: String) {
    Text(
        text = texto,
        modifier = Modifier.fillMaxWidth(),
        fontWeight = FontWeight.Bold
    )
}

// Combo box con los tipos de comunicación.
@Composable
fun SelectorTipoComunicacion(
    seleccionado: String,
    onSeleccionar: (String) -> Unit
) {
    var expandido by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = {
                expandido = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = seleccionado,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Ver opciones"
            )
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = {
                expandido = false
            }
        ) {
            TIPOS_COMUNICACION.forEach { tipo ->
                DropdownMenuItem(
                    text = {
                        Text(tipo)
                    },
                    onClick = {
                        onSeleccionar(tipo)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
fun SelectorPreferenciaInterfaz(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    Column {
        PREFERENCIAS_INTERFAZ.forEach { opcion ->
            RadioOption(
                texto = opcion,
                seleccionada = seleccionada == opcion,
                onClick = {
                    onSeleccionar(opcion)
                }
            )
        }
    }
}

/*
 * Grilla 2 x 2.
 *
 * Evitamos LazyVerticalGrid dentro de verticalScroll
 * para no generar conflictos de scroll.
 */
@Composable
fun GrillaOpcionesAccesibilidad(
    seleccionadas: Set<String>,
    onCambio: (Set<String>) -> Unit
) {
    OPCIONES_ACCESIBILIDAD
        .chunked(2)
        .forEach { fila ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                fila.forEach { opcion ->

                    val seleccionada = seleccionadas.contains(opcion)

                    val cambiar = {
                        onCambio(
                            if (seleccionada) {
                                seleccionadas - opcion
                            } else {
                                seleccionadas + opcion
                            }
                        )
                    }

                    OutlinedCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { cambiar() },
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = seleccionada,
                                onCheckedChange = { cambiar() }
                            )

                            Text(
                                text = opcion,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                if (fila.size == 1) {
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
}

@Composable
fun RadioOption(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = seleccionada,
            onClick = onClick
        )

        Text(
            text = texto
        )
    }
}
