package cl.duoc.comunicaplusrs.ui.screens

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.ui.components.BarraSuperior
import cl.duoc.comunicaplusrs.ui.components.MensajeEstado
import cl.duoc.comunicaplusrs.utils.ElegirTelefono
import cl.duoc.comunicaplusrs.utils.LARGO_MAXIMO_MENSAJE
import cl.duoc.comunicaplusrs.utils.LectorVoz
import cl.duoc.comunicaplusrs.utils.crearIntentSms
import cl.duoc.comunicaplusrs.utils.leerContacto
import cl.duoc.comunicaplusrs.viewmodel.EscribirViewModel

// Frases de uso común que se pueden tocar para no escribirlas cada vez.
private val FRASES_RAPIDAS = listOf(
    "Hola, soy una persona sorda.",
    "¿Me lo puede escribir, por favor?",
    "Hable más lento, por favor.",
    "Necesito ayuda.",
    "Gracias por su ayuda."
)

@Composable
fun EscribirScreen(
    onVolver: () -> Unit,
    viewModel: EscribirViewModel = viewModel(factory = EscribirViewModel.Factory)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var texto by rememberSaveable {
        mutableStateOf("")
    }

    var pantallaCompleta by rememberSaveable {
        mutableStateOf(false)
    }

    val lector = remember {
        LectorVoz(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            lector.cerrar()
        }
    }

    // Después de guardar una frase se limpia el campo.
    LaunchedEffect(uiState.guardadosOk) {
        if (uiState.guardadosOk > 0) {
            texto = ""
        }
    }

    val elegirContacto = rememberLauncherForActivityResult(ElegirTelefono()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val contacto = leerContacto(context.contentResolver, uri)

        if (contacto == null || contacto.numero.isBlank()) {
            viewModel.mostrarMensaje("No se pudo leer el número del contacto.", esError = true)
            return@rememberLauncherForActivityResult
        }

        try {
            context.startActivity(crearIntentSms(contacto.numero, texto))
            viewModel.mostrarMensaje("Mensaje listo para enviar a ${contacto.nombre}.", esError = false)
        } catch (e: ActivityNotFoundException) {
            viewModel.mostrarMensaje("No hay una app de mensajes en este teléfono.", esError = true)
        }
    }

    val leer: (String) -> Unit = { contenido ->
        if (contenido.isBlank()) {
            viewModel.mostrarMensaje("Primero escribe un mensaje.", esError = true)
        } else if (!lector.leer(contenido)) {
            viewModel.mostrarMensaje("La lectura en voz alta no está disponible en este teléfono.", esError = true)
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Escribir",
                onVolver = onVolver
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text(
                    text = "Escribe lo que quieres decir. La app lo lee en voz alta o lo muestra en grande para que la otra persona lo vea."
                )
            }

            item {
                OutlinedTextField(
                    value = texto,
                    onValueChange = {
                        if (it.length <= LARGO_MAXIMO_MENSAJE) {
                            texto = it
                        }
                    },
                    label = {
                        Text("Tu mensaje")
                    },
                    textStyle = MaterialTheme.typography.titleLarge,
                    minLines = 3,
                    supportingText = {
                        Text("${texto.length}/$LARGO_MAXIMO_MENSAJE")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FRASES_RAPIDAS) { frase ->
                        SuggestionChip(
                            onClick = {
                                texto = frase
                            },
                            label = {
                                Text(frase)
                            }
                        )
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { leer(texto) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        TextoConIcono("LEER", Icons.AutoMirrored.Filled.VolumeUp)
                    }

                    FilledTonalButton(
                        onClick = {
                            if (texto.isBlank()) {
                                viewModel.mostrarMensaje("Primero escribe un mensaje.", esError = true)
                            } else {
                                pantallaCompleta = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        TextoConIcono("MOSTRAR", Icons.Filled.Fullscreen)
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.guardarFrase(texto)
                        },
                        enabled = !uiState.guardando,
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        TextoConIcono(
                            if (uiState.idEditando == null) "GUARDAR" else "ACTUALIZAR",
                            Icons.Filled.Save
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            if (texto.isBlank()) {
                                viewModel.mostrarMensaje("Primero escribe un mensaje.", esError = true)
                            } else {
                                elegirContacto.launch(Unit)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        TextoConIcono("SMS", Icons.Filled.ContactPhone)
                    }
                }

                if (uiState.idEditando != null) {
                    TextButton(
                        onClick = {
                            viewModel.cancelarEdicion()
                            texto = ""
                        }
                    ) {
                        Text("Cancelar edición")
                    }
                }

                MensajeEstado(
                    mensaje = uiState.mensaje,
                    esError = uiState.esError
                )
            }

            item {
                Text(
                    text = "Mis frases guardadas (${uiState.frases.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.frases.isEmpty()) {
                item {
                    Text("Todavía no tienes frases guardadas.")
                }
            }

            items(
                items = uiState.frases,
                key = { it.id }
            ) { frase ->
                TarjetaFrase(
                    frase = frase,
                    onUsar = {
                        texto = frase.texto
                    },
                    onLeer = {
                        leer(frase.texto)
                    },
                    onEditar = {
                        texto = frase.texto
                        viewModel.editar(frase)
                    },
                    onEliminar = {
                        viewModel.eliminar(frase)
                    }
                )
            }
        }
    }

    if (pantallaCompleta) {
        MensajePantallaCompleta(
            texto = texto,
            onCerrar = {
                pantallaCompleta = false
            }
        )
    }
}

@Composable
private fun TextoConIcono(
    texto: String,
    icono: ImageVector
) {
    Icon(
        imageVector = icono,
        contentDescription = null
    )
    Spacer(
        modifier = Modifier.width(6.dp)
    )
    Text(
        text = texto
    )
}

@Composable
private fun TarjetaFrase(
    frase: Mensaje,
    onUsar: () -> Unit,
    onLeer: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    OutlinedCard(
        onClick = onUsar,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = frase.texto,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onLeer) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Leer frase"
                )
            }

            IconButton(onClick = onEditar) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar frase"
                )
            }

            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar frase",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Muestra el mensaje con letras muy grandes para mostrarle el teléfono a otra persona.
@Composable
private fun MensajePantallaCompleta(
    texto: String,
    onCerrar: () -> Unit
) {
    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onCerrar() }
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = texto,
                    fontSize = 40.sp,
                    lineHeight = 48.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                Text(
                    text = "Toca la pantalla para cerrar",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
