package cl.duoc.comunicaplusrs.robolectric

import android.app.Application
import android.appwidget.AppWidgetManager
import android.view.View
import androidx.test.core.app.ApplicationProvider
import cl.duoc.comunicaplusrs.MainActivity
import cl.duoc.comunicaplusrs.R
import cl.duoc.comunicaplusrs.navigation.Routes
import cl.duoc.comunicaplusrs.widget.AccesoRapidoWidget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AccesoRapidoWidgetTest {

    private val app = ApplicationProvider.getApplicationContext<Application>()

    @Test
    fun intentDelWidget_abreMainActivityConElDestino() {
        val intent = AccesoRapidoWidget.crearIntentDestino(app, Routes.HABLAR)

        assertEquals(MainActivity::class.java.name, intent.component?.className)
        assertEquals(Routes.HABLAR, intent.getStringExtra(MainActivity.EXTRA_DESTINO))
    }

    @Test
    fun alTocarEscribirEnElWidget_seAbreLaAppEnEscribir() {
        val manager = AppWidgetManager.getInstance(app)

        // Robolectric crea el widget y llama a onUpdate como lo haría el launcher.
        val idWidget = shadowOf(manager).createWidget(
            AccesoRapidoWidget::class.java,
            R.layout.widget_acceso_rapido
        )
        val vista: View = shadowOf(manager).getViewFor(idWidget)

        vista.findViewById<View>(R.id.btnWidgetEscribir).performClick()

        val abierta = shadowOf(app).nextStartedActivity
        assertNotNull(abierta)
        assertEquals(Routes.ESCRIBIR, abierta.getStringExtra(MainActivity.EXTRA_DESTINO))
    }
}
