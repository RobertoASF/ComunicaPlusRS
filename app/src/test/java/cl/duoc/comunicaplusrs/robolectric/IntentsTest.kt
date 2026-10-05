package cl.duoc.comunicaplusrs.robolectric

import android.content.Intent
import android.speech.RecognizerIntent
import cl.duoc.comunicaplusrs.utils.crearIntentMapa
import cl.duoc.comunicaplusrs.utils.crearIntentMapaWeb
import cl.duoc.comunicaplusrs.utils.crearIntentReconocerVoz
import cl.duoc.comunicaplusrs.utils.crearIntentSms
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Intent y Uri son clases de Android, por eso estas pruebas corren con Robolectric.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IntentsTest {

    @Test
    fun intentSms_dejaSoloElNumeroYAgregaElMensaje() {
        val intent = crearIntentSms("+56 9 1234-5678", "Hola, soy una persona sorda.")

        assertEquals(Intent.ACTION_SENDTO, intent.action)
        assertEquals("smsto:+56912345678", intent.data.toString())
        assertEquals("Hola, soy una persona sorda.", intent.getStringExtra("sms_body"))
    }

    @Test
    fun intentMapa_usaLasCoordenadasYElNombre() {
        val intent = crearIntentMapa(-33.4372, -70.6506, "Pixel 3a")

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("geo", intent.data?.scheme)
        assertTrue(intent.data.toString().startsWith("geo:-33.4372,-70.6506"))
        assertTrue(intent.data.toString().contains("Pixel%203a"))
    }

    @Test
    fun intentMapaWeb_abreGoogleMapsEnElNavegador() {
        val intent = crearIntentMapaWeb(-33.4372, -70.6506)

        assertEquals("https", intent.data?.scheme)
        assertEquals("-33.4372,-70.6506", intent.data?.getQueryParameter("query"))
    }

    @Test
    fun intentVoz_pideReconocimientoEnEspanolDeChile() {
        val intent = crearIntentReconocerVoz()

        assertEquals(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, intent.action)
        assertEquals("es-CL", intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE))
    }
}
