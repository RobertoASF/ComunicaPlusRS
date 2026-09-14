package cl.duoc.comunicaplusrs.utils

// Esta extensión se usa para mantener la validación del correo en un solo lugar.
// Revisa que tenga algo antes del @ y un punto en la parte del dominio.
fun String.esCorreoValido(): Boolean {
    return isNotBlank() &&
            contains("@") &&
            substringBefore("@").isNotEmpty() &&
            substringAfter("@").contains(".")
}

// La contraseña debe tener al menos 4 caracteres, igual que en la entrega anterior.
fun validarPassword(password: String): Boolean {
    return password.length >= 4
}

// Recibe una lambda con la regla que se quiere revisar,
// así la misma función sirve para validar distintos campos del formulario.
fun validarCampo(
    valor: String,
    validacion: (String) -> Boolean
): Boolean {
    return validacion(valor.trim())
}
