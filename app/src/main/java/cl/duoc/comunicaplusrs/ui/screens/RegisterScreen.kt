package cl.duoc.comunicaplusrs.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.comunicaplusrs.data.UsuarioRepository
import cl.duoc.comunicaplusrs.model.Usuario

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit
) {

    var nombre by remember {
        mutableStateOf("")
    }

    var correo by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var tipoComunicacion by remember {
        mutableStateOf("Texto")
    }

    var preferenciaInterfaz by remember {
        mutableStateOf("Estándar")
    }

    var aceptaTerminos by remember {
        mutableStateOf(false)
    }

    var dropdownExpandido by remember {
        mutableStateOf(false)
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    var registroCorrecto by remember {
        mutableStateOf(false)
    }

    var actualizacionLista by remember {
        mutableIntStateOf(0)
    }

    val opcionesAccesibilidad =
        listOf(
            "Texto grande",
            "Alto contraste",
            "Alertas visuales",
            "Vibración"
        )

    var opcionesSeleccionadas by remember {
        mutableStateOf(setOf<String>())
    }

    val usuariosRegistrados = remember(actualizacionLista) {
        UsuarioRepository.obtenerUsuarios()
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
                Text("Correo electronico")
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
            singleLine = true,
            visualTransformation =
            PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Tipo de comunicación",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedButton(
                onClick = {
                    dropdownExpandido = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = tipoComunicacion
                )
            }

            DropdownMenu(
                expanded = dropdownExpandido,
                onDismissRequest = {
                    dropdownExpandido = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("Texto")
                    },
                    onClick = {
                        tipoComunicacion = "Texto"
                        dropdownExpandido = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Lengua de señas")
                    },
                    onClick = {
                        tipoComunicacion =
                            "Lengua de señas"

                        dropdownExpandido = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Texto y lengua de señas")
                    },
                    onClick = {
                        tipoComunicacion =
                            "Texto y lengua de señas"

                        dropdownExpandido = false
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Preferencia de interfaz",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )

        RadioOption(
            texto = "Estándar",
            seleccionada =
            preferenciaInterfaz == "Estándar",
            onClick = {
                preferenciaInterfaz = "Estándar"
            }
        )

        RadioOption(
            texto = "Texto grande",
            seleccionada =
            preferenciaInterfaz == "Texto grande",
            onClick = {
                preferenciaInterfaz = "Texto grande"
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Opciones de accesibilidad",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        /*
         * Grilla 2 x 2.
         *
         * Evitamos LazyVerticalGrid dentro de verticalScroll
         * para no generar conflictos de scroll.
         */
        opcionesAccesibilidad
            .chunked(2)
            .forEach { fila ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
                ) {

                    fila.forEach { opcion ->

                        val seleccionada =
                            opcionesSeleccionadas
                                .contains(opcion)

                        OutlinedCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {

                                    opcionesSeleccionadas =
                                        if (seleccionada) {

                                            opcionesSeleccionadas -
                                                    opcion

                                        } else {

                                            opcionesSeleccionadas +
                                                    opcion
                                        }
                                },
                            shape =
                            RoundedCornerShape(12.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment =
                                Alignment.CenterVertically
                            ) {

                                Checkbox(
                                    checked = seleccionada,
                                    onCheckedChange = {

                                        opcionesSeleccionadas =
                                            if (seleccionada) {

                                                opcionesSeleccionadas -
                                                        opcion

                                            } else {

                                                opcionesSeleccionadas +
                                                        opcion
                                            }
                                    }
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
                    aceptaTerminos =
                        !aceptaTerminos
                }
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {

                when {

                    nombre.isBlank() -> {

                        mensaje =
                            "Debes ingresar tu nombre."

                        registroCorrecto = false
                    }

                    correo.isBlank() -> {

                        mensaje =
                            "Debes ingresar tu correo."

                        registroCorrecto = false
                    }

                    !correo.contains("@") -> {

                        mensaje =
                            "Ingresa un correo válido."

                        registroCorrecto = false
                    }

                    password.length < 4 -> {

                        mensaje =
                            "La contraseña debe tener al menos 4 caracteres."

                        registroCorrecto = false
                    }

                    !aceptaTerminos -> {

                        mensaje =
                            "Debes aceptar los términos y condiciones."

                        registroCorrecto = false
                    }

                    else -> {

                        val usuario = Usuario(
                            nombre = nombre.trim(),
                            correo = correo.trim(),
                            password = password,
                            tipoComunicacion =
                            tipoComunicacion,
                            preferenciaInterfaz =
                            preferenciaInterfaz,
                            aceptaTerminos =
                            aceptaTerminos,
                            opcionesAccesibilidad =
                            opcionesSeleccionadas
                        )

                        when (
                            UsuarioRepository
                                .registrarUsuario(usuario)
                        ) {

                            UsuarioRepository
                                .ResultadoRegistro
                                .EXITO -> {

                                mensaje =
                                    "Usuario registrado correctamente."

                                registroCorrecto = true

                                nombre = ""
                                correo = ""
                                password = ""

                                aceptaTerminos = false

                                opcionesSeleccionadas =
                                    emptySet()

                                actualizacionLista++
                            }

                            UsuarioRepository
                                .ResultadoRegistro
                                .CORREO_EXISTENTE -> {

                                mensaje =
                                    "El correo ya se encuentra registrado."

                                registroCorrecto = false
                            }

                            UsuarioRepository
                                .ResultadoRegistro
                                .LIMITE_ALCANZADO -> {

                                mensaje =
                                    "Se alcanzó el máximo de 5 usuarios."

                                registroCorrecto = false
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {

            Text(
                text = "REGISTRAR"
            )
        }

        if (mensaje.isNotBlank()) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = mensaje,
                color = if (registroCorrecto) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text =
            "Usuarios registrados " +
                    "(${usuariosRegistrados.size}/5)",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (usuariosRegistrados.isEmpty()) {

            Text(
                text =
                "Todavía no existen usuarios registrados.",
                modifier = Modifier.fillMaxWidth()
            )

        } else {

            usuariosRegistrados
                .forEachIndexed { index, usuario ->

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 4.dp
                            )
                    ) {

                        Column(
                            modifier =
                            Modifier.padding(12.dp)
                        ) {

                            Text(
                                text =
                                "${index + 1}. ${usuario.nombre}",
                                fontWeight =
                                FontWeight.Bold
                            )

                            Text(
                                text = usuario.correo
                            )

                            Text(
                                text =
                                "Comunicación: " +
                                        usuario.tipoComunicacion,
                                fontSize = 13.sp
                            )
                        }
                    }
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

        Spacer(
            modifier = Modifier.height(32.dp)
        )
    }
}

@Composable
private fun RadioOption(
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
        verticalAlignment =
        Alignment.CenterVertically
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