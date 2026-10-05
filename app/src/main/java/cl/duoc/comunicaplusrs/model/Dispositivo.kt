package cl.duoc.comunicaplusrs.model

import com.google.firebase.firestore.DocumentId

// Última ubicación conocida de cada teléfono donde el usuario inició sesión.
data class Dispositivo(
    @DocumentId
    val id: String = "",
    val nombre: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val precision: Float = 0f,
    val direccion: String = "",
    val fecha: Long = 0L
)
