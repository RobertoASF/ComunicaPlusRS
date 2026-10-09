package cl.duoc.comunicaplusrs.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.comunicaplusrs.ui.components.BarraSuperior
import cl.duoc.comunicaplusrs.ui.components.BotonPrincipal
import cl.duoc.comunicaplusrs.ui.components.GrillaOpcionesAccesibilidad
import cl.duoc.comunicaplusrs.ui.components.MensajeEstado
import cl.duoc.comunicaplusrs.ui.components.SelectorPreferenciaInterfaz
import cl.duoc.comunicaplusrs.ui.components.SelectorTipoComunicacion
import cl.duoc.comunicaplusrs.ui.components.TituloSeccion
import cl.duoc.comunicaplusrs.viewmodel.PerfilViewModel

// Permite leer, actualizar y eliminar los datos del usuario (CRUD del perfil).
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onVolver: () -> Unit,
    onCuentaEliminada: () -> Unit
) {
    val usuario by viewModel.usuario.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Los campos se cargan con los datos de Firestore cuando llegan.
    var nombre by remember(usuario?.id) {
        mutableStateOf(usuario?.nombre.orEmpty())
    }

    var tipoComunicacion by remember(usuario?.id) {
        mutableStateOf(usuario?.tipoComunicacion ?: "Texto")
    }

    var preferenciaInterfaz by remember(usuario?.id) {
        mutableStateOf(usuario?.preferenciaInterfaz ?: "Estándar")
    }

    var opciones by remember(usuario?.id) {
        mutableStateOf(usuario?.opcionesAccesibilidad.orEmpty().toSet())
    }

    var mostrarDialogo by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.limpiarMensaje()
    }

    LaunchedEffect(uiState.cuentaEliminada) {
        if (uiState.cuentaEliminada) {
            onCuentaEliminada()
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Mi perfil",
                onVolver = onVolver
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier.widthIn(max = 560.dp)
            ) {

                Text(
                    text = "Correo: ${viewModel.correo}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
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
                    modifier = Modifier.height(20.dp)
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
                    modifier = Modifier.height(20.dp)
                )

                TituloSeccion("Preferencia de interfaz")

                SelectorPreferenciaInterfaz(
                    seleccionada = preferenciaInterfaz,
                    onSeleccionar = {
                        preferenciaInterfaz = it
                    }
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                TituloSeccion("Opciones de accesibilidad")

                Text(
                    text = "Los cambios se aplican en toda la app al guardar.",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                GrillaOpcionesAccesibilidad(
                    seleccionadas = opciones,
                    onCambio = {
                        opciones = it
                    }
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                BotonPrincipal(
                    texto = "GUARDAR CAMBIOS",
                    icono = Icons.Filled.Save,
                    cargando = uiState.cargando,
                    onClick = {
                        viewModel.guardarCambios(
                            nombre = nombre,
                            tipoComunicacion = tipoComunicacion,
                            preferenciaInterfaz = preferenciaInterfaz,
                            opcionesAccesibilidad = opciones
                        )
                    }
                )

                MensajeEstado(
                    mensaje = uiState.mensaje,
                    esError = uiState.esError
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                OutlinedButton(
                    onClick = {
                        mostrarDialogo = true
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = null
                    )
                    Text(
                        text = "ELIMINAR MI CUENTA"
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoEliminarCuenta(
            onConfirmar = { password ->
                mostrarDialogo = false
                viewModel.eliminarCuenta(password)
            },
            onCancelar = {
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun DialogoEliminarCuenta(
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var password by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text("Eliminar cuenta")
        },
        text = {
            Column {
                Text("Se borrarán tu perfil, tus frases, tu historial y tus dispositivos. Esta acción no se puede deshacer.")

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
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmar(password)
                }
            ) {
                Text(
                    text = "ELIMINAR",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("CANCELAR")
            }
        }
    )
}
