package cl.duoc.comunicaplusrs.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.comunicaplusrs.data.PreferenciasLocales
import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.model.PreferenciasAccesibilidad
import cl.duoc.comunicaplusrs.ui.components.BarraSuperior
import cl.duoc.comunicaplusrs.ui.components.FilaTabla
import cl.duoc.comunicaplusrs.ui.components.MensajeEstado
import cl.duoc.comunicaplusrs.utils.crearIntentReconocerVoz
import cl.duoc.comunicaplusrs.utils.formatearFechaCorta
import cl.duoc.comunicaplusrs.utils.vibrarAviso
import cl.duoc.comunicaplusrs.viewmodel.HablarViewModel
import kotlinx.coroutines.delay

private val pesosHistorial = listOf(1f, 2.6f, 0.6f)

@Composable
fun HablarScreen(
    preferencias: PreferenciasAccesibilidad,
    onVolver: () -> Unit,
    onResponder: () -> Unit,
    viewModel: HablarViewModel = viewModel(factory = HablarViewModel.Factory)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val preferenciasLocales = remember {
        PreferenciasLocales(context)
    }

    var tamanoTexto by remember {
        mutableFloatStateOf(preferenciasLocales.tamanoTextoHablar)
    }

    var alertaActiva by remember {
        mutableStateOf(false)
    }

    // El reconocimiento de voz lo hace el servicio de Google del teléfono.
    val reconocerVoz = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            val texto = resultado.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                .orEmpty()

            viewModel.textoReconocido(texto)
        }
    }

    // Avisos para el usuario cuando llega un texto nuevo, según su perfil.
    LaunchedEffect(uiState.textosRecibidos) {
        if (uiState.textosRecibidos == 0) return@LaunchedEffect

        if (preferencias.vibracion) {
            context.vibrarAviso()
        }

        if (preferencias.alertasVisuales) {
            repeat(3) {
                alertaActiva = true
                delay(250)
                alertaActiva = false
                delay(250)
            }
        }
    }

    val colorBorde by animateColorAsState(
        targetValue = if (alertaActiva) {
            MaterialTheme.colorScheme.error
        } else {
            Color.Transparent
        },
        animationSpec = tween(150),
        label = "alerta"
    )

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Hablar",
                onVolver = onVolver
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier.widthIn(max = 640.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Pide a la otra persona que hable cerca del teléfono. Lo que diga aparecerá escrito en la pantalla.",
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Button(
                    onClick = {
                        try {
                            reconocerVoz.launch(crearIntentReconocerVoz())
                        } catch (e: ActivityNotFoundException) {
                            viewModel.mostrarError("Este teléfono no tiene reconocimiento de voz disponible.")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )
                    Text(
                        text = "TOCAR PARA ESCUCHAR",
                        fontSize = 18.sp
                    )
                }

                MensajeEstado(
                    mensaje = uiState.mensaje,
                    esError = uiState.esError
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Card(
                    border = BorderStroke(4.dp, colorBorde),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
                ) {
                    Text(
                        text = uiState.ultimoTexto.ifBlank { "Aquí aparecerá lo que te digan." },
                        fontSize = tamanoTexto.sp,
                        lineHeight = (tamanoTexto * 1.2f).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .semantics { liveRegion = LiveRegionMode.Polite }
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Tamaño del texto: ${tamanoTexto.toInt()}",
                    modifier = Modifier.fillMaxWidth()
                )

                Slider(
                    value = tamanoTexto,
                    onValueChange = {
                        tamanoTexto = it
                    },
                    onValueChangeFinished = {
                        preferenciasLocales.tamanoTextoHablar = tamanoTexto
                    },
                    valueRange = 18f..56f
                )

                OutlinedButton(
                    onClick = onResponder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Keyboard,
                        contentDescription = null
                    )
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    Text("RESPONDER ESCRIBIENDO")
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Historial (${uiState.historial.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )

                    if (uiState.historial.isNotEmpty()) {
                        TextButton(
                            onClick = viewModel::borrarHistorial
                        ) {
                            Text("Borrar todo")
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                if (uiState.historial.isEmpty()) {
                    Text(
                        text = "Todavía no hay conversaciones guardadas.",
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    TablaHistorial(
                        historial = uiState.historial,
                        onEliminar = viewModel::eliminar
                    )
                }
            }
        }
    }
}

// Tabla con columnas: fecha, texto escuchado y botón para borrar la fila.
@Composable
private fun TablaHistorial(
    historial: List<Mensaje>,
    onEliminar: (Mensaje) -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        FilaTabla(
            celdas = listOf("Fecha", "Texto", ""),
            pesos = pesosHistorial,
            esEncabezado = true
        )

        historial.forEach { mensaje ->

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = formatearFechaCorta(mensaje.fecha),
                    fontSize = 12.sp,
                    modifier = Modifier.weight(pesosHistorial[0])
                )
                Text(
                    text = mensaje.texto,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(pesosHistorial[1])
                )
                IconButton(
                    onClick = { onEliminar(mensaje) },
                    modifier = Modifier.weight(pesosHistorial[2])
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar del historial",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
