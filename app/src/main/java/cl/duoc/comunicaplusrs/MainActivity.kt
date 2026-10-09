package cl.duoc.comunicaplusrs

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import cl.duoc.comunicaplusrs.navigation.ComunicaPlusApp

// Se usa FragmentActivity (en vez de ComponentActivity) porque la pantalla
// Buscar dispositivo muestra un Fragment dentro de Compose.
class MainActivity : FragmentActivity() {

    // Pantalla que pidió abrir el widget (Escribir o Hablar).
    private var destinoWidget by mutableStateOf<String?>(null)

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            destinoWidget = intent.getStringExtra(EXTRA_DESTINO)
        }

        setContent {

            ComunicaPlusApp(
                destinoWidget = destinoWidget,
                onDestinoAbierto = {
                    destinoWidget = null
                }
            )
        }
    }

    // Si la app ya estaba abierta, el widget llega por aquí.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        destinoWidget = intent.getStringExtra(EXTRA_DESTINO)
    }

    companion object {
        const val EXTRA_DESTINO = "destino"
    }
}
