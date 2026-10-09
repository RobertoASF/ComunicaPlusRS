package cl.duoc.comunicaplusrs.utils


fun String.esCorreoValido(): Boolean {
    return isNotBlank() &&
            contains("@") &&
            substringBefore("@").isNotEmpty() &&
            substringAfter("@").contains(".")
}

// Firebase Authentication exige mínimo 6 caracteres (antes eran 4).
const val LARGO_MINIMO_PASSWORD = 6

fun validarPassword(password: String): Boolean {
    return password.length >= LARGO_MINIMO_PASSWORD
}

// Recibe una lambda con la regla que se quiere revisar,
// así la misma función sirve para validar distintos campos del formulario.
fun validarCampo(
    valor: String,
    validacion: (String) -> Boolean
): Boolean {
    return validacion(valor.trim())
}

// Largo máximo de las frases de Escribir, para que se lean bien en pantalla completa.
const val LARGO_MAXIMO_MENSAJE = 200

fun validarMensaje(texto: String): Boolean {
    return validarCampo(texto) {
        it.isNotEmpty() && it.length <= LARGO_MAXIMO_MENSAJE
    }
}
