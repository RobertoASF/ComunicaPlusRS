package cl.duoc.comunicaplusrs

import cl.duoc.comunicaplusrs.utils.calcularDistanciaMetros
import cl.duoc.comunicaplusrs.utils.formatearDistancia
import org.junit.Assert.assertEquals
import org.junit.Test

// Pruebas escritas antes de la implementación (TDD).
// Primero fallan y luego se programa lo mínimo para que pasen.
class DistanciaTest {

    @Test
    fun mismoPunto_distanciaCero() {
        val distancia = calcularDistanciaMetros(-33.4489, -70.6693, -33.4489, -70.6693)

        assertEquals(0.0, distancia, 0.001)
    }

    @Test
    fun unGradoDeLongitudEnElEcuador_midenAproximadamente111Km() {
        val distancia = calcularDistanciaMetros(0.0, 0.0, 0.0, 1.0)

        assertEquals(111_195.0, distancia, 1.0)
    }

    @Test
    fun santiagoAValparaiso_midenAproximadamente99Km() {
        val distancia = calcularDistanciaMetros(-33.4372, -70.6506, -33.0472, -71.6127)

        assertEquals(99_429.0, distancia, 50.0)
    }

    @Test
    fun laDistanciaEsIgualEnAmbosSentidos() {
        val ida = calcularDistanciaMetros(-33.4372, -70.6506, -33.4263, -70.6200)
        val vuelta = calcularDistanciaMetros(-33.4263, -70.6200, -33.4372, -70.6506)

        assertEquals(ida, vuelta, 0.001)
    }

    @Test
    fun formatear_menosDeUnKilometro_seMuestraEnMetros() {
        assertEquals("850 m", formatearDistancia(849.6))
        assertEquals("0 m", formatearDistancia(0.0))
    }

    @Test
    fun formatear_desdeUnKilometro_seMuestraConUnDecimal() {
        assertEquals("3,1 km", formatearDistancia(3087.43))
        assertEquals("99,4 km", formatearDistancia(99_429.0))
    }

    @Test
    fun formatear_casiMilMetros_pasaAKilometros() {
        // 999,6 m se redondea a 1000 m, por lo que debe mostrarse en km.
        assertEquals("1,0 km", formatearDistancia(999.6))
    }
}
