package cl.duoc.comunicaplusrs.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import cl.duoc.comunicaplusrs.model.PreferenciasAccesibilidad
import cl.duoc.comunicaplusrs.ui.components.BarraSuperior
import cl.duoc.comunicaplusrs.ui.components.BurbujaMensaje
import cl.duoc.comunicaplusrs.ui.components.MensajeEstado
import cl.duoc.comunicaplusrs.utils.LARGO_MAXIMO_MENSAJE
import cl.duoc.comunicaplusrs.utils.LectorVoz
import cl.duoc.comunicaplusrs.utils.crearIntentReconocerVoz
import cl.duoc.comunicaplusrs.utils.vibrarAviso
import cl.duoc.comunicaplusrs.viewmodel.HablarViewModel
import kotlinx.coroutines.delay

// Respuestas cortas que se leen en voz alta con un solo toque.
private val RESPUESTAS_RAPIDAS = listOf(
    "Sí",
    "No",
    "¿Puede repetirlo?",
    "Un momento, por favor",
    "Muchas gracias"
)

@Composable
fun HablarScreen(
    preferencias: PreferenciasAccesibilidad,
    onVolver: () -> Unit,
    onVerHistorial: () -> Unit,
    onAyuda: () -> Unit,
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

    var respuesta by rememberSaveable {
        mutableStateOf("")
    }

    val lector = remember {
        LectorVoz(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            lector.cerrar()
        }
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

    // Después de guardar la respuesta se limpia el campo.
    LaunchedEffect(uiState.respuestasOk) {
        if (uiState.respuestasOk > 0) {
            respuesta = ""
        }
    }

    val responder: (String) -> Unit = { texto ->
        if (texto.isBlank()) {
            viewModel.mostrarError("Primero escribe tu respuesta.")
        } else {
            viewModel.responder(texto)
            if (!lector.leer(texto)) {
                viewModel.mostrarError("La lectura en voz alta no está disponible en este teléfono.")
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
                onVolver = onVolver,
                onAyuda = onAyuda
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
                    // El texto nuevo aparece con un fundido para que se note el cambio.
                    AnimatedContent(
                        targetState = uiState.ultimoTexto,
                        transitionSpec = {
                            fadeIn(tween(400)) togetherWith fadeOut(tween(200))
                        },
                        label = "ultimoTexto"
                    ) { texto ->
                        Text(
                            text = texto.ifBlank { "Aquí aparecerá lo que te digan." },
                            fontSize = tamanoTexto.sp,
                            lineHeight = (tamanoTexto * 1.2f).sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .semantics { liveRegion = LiveRegionMode.Polite }
                        )
                    }
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

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Text(
                    text = "Tu respuesta",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedTextField(
                    value = respuesta,
                    onValueChange = {
                        if (it.length <= LARGO_MAXIMO_MENSAJE) {
                            respuesta = it
                        }
                    },
                    label = {
                        Text("Escribe lo que quieres responder")
                    },
                    textStyle = MaterialTheme.typography.titleMedium,
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(RESPUESTAS_RAPIDAS) { frase ->
                        SuggestionChip(
                            onClick = {
                                responder(frase)
                            },
                            label = {
                                Text(frase)
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                FilledTonalButton(
                    onClick = {
                        responder(respuesta)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null
                    )
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    Text("RESPONDER EN VOZ ALTA")
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Conversación reciente",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = onVerHistorial
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )
                        Text("Ver historial")
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                if (uiState.recientes.isEmpty()) {
                    Text(
                        text = "Todavía no hay mensajes en la conversación.",
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.recientes.forEach { mensaje ->
                            BurbujaMensaje(
                                mensaje = mensaje
                            )
                        }
                    }
                }
            }
        }
    }
}
