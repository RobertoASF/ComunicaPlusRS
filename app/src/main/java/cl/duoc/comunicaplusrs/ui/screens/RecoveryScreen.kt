package cl.duoc.comunicaplusrs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.comunicaplusrs.data.UsuarioRepository
import cl.duoc.comunicaplusrs.utils.esCorreoValido

@Composable
fun RecoveryScreen(
    onBackToLogin: () -> Unit
) {

    var correo by remember {
        mutableStateOf("")
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    var encontrado by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Recuperar contraseña",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Ingresa el correo electronico utilizado durante tu registro.",
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
            },
            label = {
                Text("Correo electronico")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {

                if (correo.isBlank()) {

                    mensaje = "Debe ingresar un correo."
                    encontrado = false

                } else if (!correo.trim().esCorreoValido()) {

                    // Se reutiliza la misma extensión del registro para revisar el formato.
                    mensaje = "Ingresa un correo válido."
                    encontrado = false

                } else {

                    val usuario =
                        UsuarioRepository.buscarPorCorreo(correo)

                    if (usuario != null) {

                        mensaje =
                            "Cuenta encontrada para ${usuario.nombre}."

                        encontrado = true

                    } else {

                        mensaje =
                            "No existe una cuenta asociada a ese correo :c"

                        encontrado = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {

            Text(
                text = "BUSCAR CUENTA"
            )
        }

        if (mensaje.isNotBlank()) {

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = mensaje,
                color = if (encontrado) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            if (encontrado) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "En una aplicación real se enviaría un paso a paso de recuperación al correo registrado.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        TextButton(
            onClick = onBackToLogin
        ) {

            Text(
                text = "Volver al inicio de sesión"
            )
        }
    }
}