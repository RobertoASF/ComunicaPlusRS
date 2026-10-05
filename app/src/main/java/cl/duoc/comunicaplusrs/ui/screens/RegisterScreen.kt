package cl.duoc.comunicaplusrs.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.comunicaplusrs.ui.components.BotonPrincipal
import cl.duoc.comunicaplusrs.ui.components.GrillaOpcionesAccesibilidad
import cl.duoc.comunicaplusrs.ui.components.MensajeEstado
import cl.duoc.comunicaplusrs.ui.components.SelectorPreferenciaInterfaz
import cl.duoc.comunicaplusrs.ui.components.SelectorTipoComunicacion
import cl.duoc.comunicaplusrs.ui.components.TituloSeccion
import cl.duoc.comunicaplusrs.utils.LARGO_MINIMO_PASSWORD
import cl.duoc.comunicaplusrs.viewmodel.RegistroViewModel

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegistroExitoso: () -> Unit,
    viewModel: RegistroViewModel = viewModel(factory = RegistroViewModel.Factory)
) {

    var nombre by rememberSaveable {
        mutableStateOf("")
    }

    var correo by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var tipoComunicacion by rememberSaveable {
        mutableStateOf("Texto")
    }

    var preferenciaInterfaz by rememberSaveable {
        mutableStateOf("Estándar")
    }

    var aceptaTerminos by rememberSaveable {
        mutableStateOf(false)
    }

    var opcionesSeleccionadas by remember {
        mutableStateOf(setOf<String>())
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Al crear la cuenta Firebase deja la sesión iniciada, así que se pasa directo al menú.
    LaunchedEffect(uiState.registroCorrecto) {
        if (uiState.registroCorrecto) {
            onRegistroExitoso()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.widthIn(max = 560.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Crear cuenta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Registra tus datos y preferencias de accesibilidad.",
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                },
                label = {
                    Text("Nombre")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                },
                label = {
                    Text("Correo electrónico")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    Text("Contraseña")
                },
                supportingText = {
                    Text("Mínimo $LARGO_MINIMO_PASSWORD caracteres")
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            TituloSeccion("Tipo de comunicación")

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            SelectorTipoComunicacion(
                seleccionado = tipoComunicacion,
                onSeleccionar = {
                    tipoComunicacion = it
                }
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            TituloSeccion("Preferencia de interfaz")

            SelectorPreferenciaInterfaz(
                seleccionada = preferenciaInterfaz,
                onSeleccionar = {
                    preferenciaInterfaz = it
                }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            TituloSeccion("Opciones de accesibilidad")

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            GrillaOpcionesAccesibilidad(
                seleccionadas = opcionesSeleccionadas,
                onCambio = {
                    opcionesSeleccionadas = it
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = aceptaTerminos,
                    onCheckedChange = {
                        aceptaTerminos = it
                    }
                )

                Text(
                    text = "Acepto los términos y condiciones",
                    modifier = Modifier.clickable {
                        aceptaTerminos = !aceptaTerminos
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            BotonPrincipal(
                texto = "REGISTRAR",
                cargando = uiState.cargando,
                onClick = {
                    viewModel.registrar(
                        nombre = nombre,
                        correo = correo,
                        password = password,
                        tipoComunicacion = tipoComunicacion,
                        preferenciaInterfaz = preferenciaInterfaz,
                        aceptaTerminos = aceptaTerminos,
                        opcionesAccesibilidad = opcionesSeleccionadas
                    )
                }
            )

            MensajeEstado(
                mensaje = uiState.mensaje,
                esError = !uiState.registroCorrecto
            )

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

            Spacer(
                modifier = Modifier.height(32.dp)
            )
        }
    }
}
