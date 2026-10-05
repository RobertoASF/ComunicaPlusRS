package cl.duoc.comunicaplusrs.model

import com.google.firebase.firestore.DocumentId

// Se usa para las frases guardadas en Escribir y para
// el historial de lo escuchado en Hablar.
data class Mensaje(
    @DocumentId
    val id: String = "",
    val texto: String = "",
    val fecha: Long = 0L
)
