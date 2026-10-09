package cl.duoc.comunicaplusrs.utils

import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.core.net.toUri

// Abre la app de mensajes con el texto ya escrito para el contacto elegido.
fun crearIntentSms(numero: String, texto: String): Intent {
    val soloNumero = numero.filter { it.isDigit() || it == '+' }

    return Intent(Intent.ACTION_SENDTO, "smsto:$soloNumero".toUri())
        .putExtra("sms_body", texto)
}

// Abre la app de mapas en el punto guardado.
fun crearIntentMapa(latitud: Double, longitud: Double, etiqueta: String): Intent {
    val uri = "geo:$latitud,$longitud?q=$latitud,$longitud(${Uri.encode(etiqueta)})".toUri()
    return Intent(Intent.ACTION_VIEW, uri)
}

// Se usa si el teléfono no tiene una app de mapas instalada.
fun crearIntentMapaWeb(latitud: Double, longitud: Double): Intent {
    val uri = "https://www.google.com/maps/search/?api=1&query=$latitud,$longitud".toUri()
    return Intent(Intent.ACTION_VIEW, uri)
}

fun crearIntentReconocerVoz(): Intent {
    return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        .putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
        .putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla cerca del teléfono")
}
