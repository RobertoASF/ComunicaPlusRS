package cl.duoc.comunicaplusrs.model

data class Usuario(
    val nombre: String,
    val correo: String,
    val password: String,
    val tipoComunicacion: String,
    val preferenciaInterfaz: String,
    val aceptaTerminos: Boolean,
    val opcionesAccesibilidad: Set<String> = emptySet()
)