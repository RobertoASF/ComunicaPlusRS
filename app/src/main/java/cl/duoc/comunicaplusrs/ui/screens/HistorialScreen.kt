package cl.duoc.comunicaplusrs.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.comunicaplusrs.ui.components.BarraSuperior
import cl.duoc.comunicaplusrs.ui.components.BurbujaMensaje
import cl.duoc.comunicaplusrs.ui.components.MensajeEstado
import cl.duoc.comunicaplusrs.utils.FiltroHistorial
import cl.duoc.comunicaplusrs.utils.LectorVoz
import cl.duoc.comunicaplusrs.viewmodel.HistorialViewModel

// Historial completo de la conversación (lo escuchado en Hablar y lo leído en voz alta).
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistorialScreen(
    onVolver: () -> Unit,
    onAyuda: () -> Unit,
    viewModel: HistorialViewModel = viewModel(factory = HistorialViewModel.Factory)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var confirmarBorrado by rememberSaveable {
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

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Historial",
                onVolver = onVolver,
                onAyuda = onAyuda,
                acciones = {
                    if (uiState.resumen.total > 0) {
                        IconButton(
                            onClick = {
                                confirmarBorrado = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                contentDescription = "Borrar todo el historial"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TarjetaResumen("Total", uiState.resumen.total, Modifier.weight(1f))
                        TarjetaResumen("Escuchados", uiState.resumen.escuchados, Modifier.weight(1f))
                        TarjetaResumen("Escritos", uiState.resumen.escritos, Modifier.weight(1f))
                    }
                }

                item {
                    OutlinedTextField(
                        value = uiState.busqueda,
                        onValueChange = viewModel::buscar,
                        label = {
                            Text("Buscar en la conversación")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (uiState.busqueda.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.buscar("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Limpiar búsqueda"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // entries entrega los valores del enum en el orden en que se declararon.
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(FiltroHistorial.entries) { filtro ->
                            FilterChip(
                                selected = uiState.filtro == filtro,
                                onClick = {
                                    viewModel.cambiarFiltro(filtro)
                                },
                                label = {
                                    Text(filtro.etiqueta)
                                }
                            )
                        }
                    }

                    MensajeEstado(
                        mensaje = uiState.mensaje,
                        esError = uiState.esError
                    )
                }

                if (uiState.grupos.isEmpty()) {
                    item {
                        HistorialVacio(
                            hayMensajes = uiState.resumen.total > 0
                        )
                    }
                }

                uiState.grupos.forEach { (dia, mensajesDelDia) ->

                    // El título del día queda fijo arriba mientras se recorren sus mensajes.
                    stickyHeader(
                        key = "dia-$dia"
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$dia (${mensajesDelDia.size})",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    items(
                        items = mensajesDelDia,
                        key = { it.id }
                    ) { mensaje ->
                        BurbujaMensaje(
                            mensaje = mensaje,
                            onLeer = {
                                if (!lector.leer(mensaje.texto)) {
                                    viewModel.mostrarError("La lectura en voz alta no está disponible en este teléfono.")
                                }
                            },
                            onEliminar = {
                                viewModel.eliminar(mensaje)
                            },
                            // animateItem anima la lista cuando un mensaje se agrega, se mueve o se elimina.
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = {
                confirmarBorrado = false
            },
            title = {
                Text("Borrar historial")
            },
            text = {
                Text("Se eliminarán todos los mensajes de la conversación. Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarBorrado = false
                        viewModel.borrarTodo()
                    }
                ) {
                    Text("Borrar todo")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmarBorrado = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// El número cambia con una animación de deslizamiento cuando llega o se borra un mensaje.
@Composable
private fun TarjetaResumen(
    titulo: String,
    cantidad: Int,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = cantidad,
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                },
                label = "cantidad"
            ) { valor ->
                Text(
                    text = valor.toString(),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = titulo,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun HistorialVacio(
    hayMensajes: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Forum,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(
            modifier = Modifier.height(12.dp)
        )
        Text(
            text = if (hayMensajes) {
                "No hay mensajes que coincidan con la búsqueda."
            } else {
                "Aquí aparecerá lo que te digan en Hablar y lo que respondas en voz alta."
            },
            textAlign = TextAlign.Center
        )
    }
}
