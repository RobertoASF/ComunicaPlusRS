package cl.duoc.comunicaplusrs

import cl.duoc.comunicaplusrs.utils.formatearCoordenadas
import cl.duoc.comunicaplusrs.utils.formatearFecha
import cl.duoc.comunicaplusrs.utils.formatearFechaCorta
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.TimeZone

class FormatosTest {

    private val utc = TimeZone.getTimeZone("UTC")

    @Test
    fun formatearFecha_usaDiaMesAnioYHora() {
        // 1 de octubre de 2026 a las 15:30 UTC
        val millis = 1_790_868_600_000L

        assertEquals("01-10-2026 15:30", formatearFecha(millis, utc))
    }

    @Test
    fun formatearFechaCorta_noMuestraElAnio() {
        val millis = 1_790_868_600_000L

        assertEquals("01-10 15:30", formatearFechaCorta(millis, utc))
    }

    @Test
    fun formatearCoordenadas_usaCincoDecimalesYPunto() {
        assertEquals("-33.43720, -70.65060", formatearCoordenadas(-33.4372, -70.6506))
    }
}
