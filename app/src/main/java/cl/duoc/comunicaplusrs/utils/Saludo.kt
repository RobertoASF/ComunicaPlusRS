package cl.duoc.comunicaplusrs.utils

// Saludo del menú principal según la hora del teléfono (0 a 23).
fun saludoSegunHora(hora: Int): String {
    return when (hora) {
        in 6..11 -> "Buenos días"
        in 12..19 -> "Buenas tardes"
        else -> "Buenas noches"
    }
}

// En el saludo basta con el primer nombre: "Roberto Sanchez" -> "Roberto".
fun primerNombre(nombre: String): String {
    return nombre.trim().substringBefore(" ")
}

fun armarSaludo(hora: Int, nombre: String?): String {
    val primero = primerNombre(nombre.orEmpty())
    val saludo = saludoSegunHora(hora)

    return if (primero.isEmpty()) "$saludo, te damos la bienvenida" else "$saludo, $primero"
}
