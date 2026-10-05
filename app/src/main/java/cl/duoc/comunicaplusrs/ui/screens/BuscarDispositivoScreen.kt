package cl.duoc.comunicaplusrs.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.compose.AndroidFragment
import cl.duoc.comunicaplusrs.ui.components.BarraSuperior
import cl.duoc.comunicaplusrs.ui.fragments.BuscarDispositivoFragment

// El contenido de esta pantalla es un Fragment con vistas XML,
// que se muestra dentro de Compose con AndroidFragment.
@Composable
fun BuscarDispositivoScreen(
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Buscar dispositivo",
                onVolver = onVolver
            )
        }
    ) { padding ->
        AndroidFragment<BuscarDispositivoFragment>(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}
