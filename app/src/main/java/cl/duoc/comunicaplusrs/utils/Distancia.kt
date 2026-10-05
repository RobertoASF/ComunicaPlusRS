package cl.duoc.comunicaplusrs.utils

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private const val RADIO_TIERRA_METROS = 6_371_000.0

// Fórmula de Haversine: distancia en línea recta entre dos puntos del mapa.
// Se hizo en Kotlin puro (sin Location de Android) para poder probarla con JUnit.
fun calcularDistanciaMetros(
    latitud1: Double,
    longitud1: Double,
    latitud2: Double,
    longitud2: Double
): Double {
    val lat1 = Math.toRadians(latitud1)
    val lat2 = Math.toRadians(latitud2)
    val difLatitud = Math.toRadians(latitud2 - latitud1)
    val difLongitud = Math.toRadians(longitud2 - longitud1)

    val a = sin(difLatitud / 2).pow(2) +
            cos(lat1) * cos(lat2) * sin(difLongitud / 2).pow(2)

    return 2 * RADIO_TIERRA_METROS * asin(sqrt(a))
}

// Menos de 1 km se muestra en metros y desde 1 km con un decimal ("3,1 km").
fun formatearDistancia(metros: Double): String {
    val redondeado = metros.roundToInt()

    return if (redondeado < 1000) {
        "$redondeado m"
    } else {
        String.format(LOCALE_CHILE, "%.1f km", metros / 1000)
    }
}
