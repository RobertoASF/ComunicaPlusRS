package cl.duoc.comunicaplusrs.model

import com.google.firebase.firestore.DocumentId

// Datos del perfil que se guardan en Firestore (colección "usuarios").
// La contraseña ya no se guarda aquí, la maneja Firebase Authentication.
// Todos los campos tienen valor por defecto porque Firestore necesita
// un constructor vacío para convertir el documento a esta clase.
data class Usuario(
    @DocumentId
    val id: String = "",
    val nombre: String = "",
    val correo: String = "",
    val tipoComunicacion: String = "Texto",
    val preferenciaInterfaz: String = "Estándar",
    val aceptaTerminos: Boolean = false,
    val opcionesAccesibilidad: List<String> = emptyList(),
    val fechaRegistro: Long = 0L
)
