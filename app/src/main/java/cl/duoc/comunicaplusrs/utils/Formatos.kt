package cl.duoc.comunicaplusrs.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

val LOCALE_CHILE: Locale = Locale("es", "CL")

fun formatearFecha(
    millis: Long,
    zona: TimeZone = TimeZone.getDefault()
): String {
    val formato = SimpleDateFormat("dd-MM-yyyy HH:mm", LOCALE_CHILE)
    formato.timeZone = zona
    return formato.format(Date(millis))
}

// Versión corta para la tabla del historial, donde hay poco espacio.
fun formatearFechaCorta(
    millis: Long,
    zona: TimeZone = TimeZone.getDefault()
): String {
    val formato = SimpleDateFormat("dd-MM HH:mm", LOCALE_CHILE)
    formato.timeZone = zona
    return formato.format(Date(millis))
}

fun formatearCoordenadas(latitud: Double, longitud: Double): String {
    return String.format(Locale.US, "%.5f, %.5f", latitud, longitud)
}
