package cl.duoc.comunicaplusrs.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

// Lee en voz alta lo que el usuario escribe, para que la otra persona lo escuche.
class LectorVoz(context: Context) : TextToSpeech.OnInitListener {

    private val tts = TextToSpeech(context.applicationContext, this)

    var disponible = false
        private set

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return

        // Se intenta con español de Chile y si no está, con español general.
        val resultado = tts.setLanguage(LOCALE_CHILE)
        disponible = if (resultado < TextToSpeech.LANG_AVAILABLE) {
            tts.setLanguage(Locale("es")) >= TextToSpeech.LANG_AVAILABLE
        } else {
            true
        }
    }

    fun leer(texto: String): Boolean {
        if (!disponible || texto.isBlank()) return false

        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "comunicaplus")
        return true
    }

    fun cerrar() {
        tts.stop()
        tts.shutdown()
    }
}
