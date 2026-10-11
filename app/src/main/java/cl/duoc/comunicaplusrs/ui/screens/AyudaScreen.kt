package cl.duoc.comunicaplusrs.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.comunicaplusrs.data.PreferenciasLocales
import cl.duoc.comunicaplusrs.model.PASOS_TUTORIAL
import cl.duoc.comunicaplusrs.model.PasoTutorial
import cl.duoc.comunicaplusrs.model.indiceDeSeccion
import cl.duoc.comunicaplusrs.navigation.Routes
import cl.duoc.comunicaplusrs.ui.components.BarraSuperior
import kotlinx.coroutines.launch

// Tutorial de uso: una página por cada función de la app.
// Se puede abrir desde el Login, desde el menú o con el botón "?" de cada pantalla.
@Composable
fun AyudaScreen(
    seccionInicial: String?,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = indiceDeSeccion(seccionInicial)
    ) {
        PASOS_TUTORIAL.size
    }

    val esUltima = pagerState.currentPage == PASOS_TUTORIAL.lastIndex

    // Una vez abierto el tutorial ya no se vuelve a invitar a verlo en el menú.
    LaunchedEffect(Unit) {
        PreferenciasLocales(context).tutorialVisto = true
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Ayuda",
                onVolver = onVolver
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Paso ${pagerState.currentPage + 1} de ${PASOS_TUTORIAL.size}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pagina ->
                PaginaTutorial(
                    paso = PASOS_TUTORIAL[pagina],
                    activa = pagina == pagerState.currentPage
                )
            }

            IndicadorPaginas(
                total = PASOS_TUTORIAL.size,
                actual = pagerState.currentPage
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    enabled = pagerState.currentPage > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Text("ANTERIOR")
                }

                Button(
                    onClick = {
                        if (esUltima) {
                            onVolver()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Text(if (esUltima) "ENTENDIDO" else "SIGUIENTE")
                }
            }
        }
    }
}

@Composable
private fun PaginaTutorial(
    paso: PasoTutorial,
    activa: Boolean
) {
    // El ícono "rebota" al llegar a la página.
    val escala by animateFloatAsState(
        targetValue = if (activa) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "escalaIcono"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(104.dp)
                    .graphicsLayer {
                        scaleX = escala
                        scaleY = escala
                    }
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconoDeSeccion(paso.seccion),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = paso.titulo,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = paso.descripcion,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    paso.consejos.forEach { consejo ->
                        Row {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )
                            Text(
                                text = consejo
                            )
                        }
                    }
                }
            }
        }
    }
}

// Puntos que muestran en qué página va el usuario; el actual se alarga y cambia de color.
@Composable
private fun IndicadorPaginas(
    total: Int,
    actual: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { indice ->
            val seleccionado = indice == actual

            val ancho by animateDpAsState(
                targetValue = if (seleccionado) 24.dp else 8.dp,
                label = "anchoPunto"
            )
            val color by animateColorAsState(
                targetValue = if (seleccionado) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                label = "colorPunto"
            )

            Box(
                modifier = Modifier
                    .size(width = ancho, height = 8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

private fun iconoDeSeccion(seccion: String): ImageVector {
    return when (seccion) {
        Routes.ESCRIBIR -> Icons.Filled.Keyboard
        Routes.HABLAR -> Icons.Filled.Mic
        Routes.HISTORIAL -> Icons.Filled.History
        Routes.BUSCAR_DISPOSITIVO -> Icons.Filled.MyLocation
        Routes.PERFIL -> Icons.Filled.ManageAccounts
        "widget" -> Icons.Filled.Widgets
        else -> Icons.Filled.EmojiPeople
    }
}
