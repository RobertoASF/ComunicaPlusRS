package cl.duoc.comunicaplusrs

import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.model.ORIGEN_ESCRITO
import cl.duoc.comunicaplusrs.model.ORIGEN_ESCUCHADO
import cl.duoc.comunicaplusrs.utils.FiltroHistorial
import cl.duoc.comunicaplusrs.utils.ResumenHistorial
import cl.duoc.comunicaplusrs.utils.agruparPorDia
import cl.duoc.comunicaplusrs.utils.filtrar
import cl.duoc.comunicaplusrs.utils.resumen
import cl.duoc.comunicaplusrs.utils.tituloDelDia
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.TimeZone

class HistorialTest {

    private val utc = TimeZone.getTimeZone("UTC")

    // 10 de octubre de 2026 a las 15:00 UTC
    private val ahora = 1_791_644_400_000L

    private val hoyEnLaManana = Mensaje(id = "1", texto = "¿Cuánto cuesta el pasaje?", fecha = 1_791_624_600_000L, origen = ORIGEN_ESCUCHADO)
    private val hoyRespuesta = Mensaje(id = "2", texto = "Necesito ir al centro", fecha = 1_791_625_000_000L, origen = ORIGEN_ESCRITO)
    private val ayer = Mensaje(id = "3", texto = "Su número de atención es el 25", fecha = 1_791_576_000_000L, origen = ORIGEN_ESCUCHADO)
    // Mensaje antiguo, guardado antes de que existiera el campo origen.
    private val jueves = Mensaje(id = "4", texto = "Pase por la ventanilla 3", fecha = 1_791_460_800_000L)

    private val conversacion = listOf(jueves, hoyEnLaManana, ayer, hoyRespuesta)

    @Test
    fun filtrarTodos_sinBusqueda_devuelveTodo() {
        assertEquals(4, conversacion.filtrar(FiltroHistorial.TODOS, "").size)
    }

    @Test
    fun filtrarEscritos_soloDevuelveLasRespuestasDelUsuario() {
        val escritos = conversacion.filtrar(FiltroHistorial.ESCRITOS, "")

        assertEquals(listOf(hoyRespuesta), escritos)
    }

    @Test
    fun filtrarEscuchados_incluyeLosMensajesSinOrigen() {
        val escuchados = conversacion.filtrar(FiltroHistorial.ESCUCHADOS, "")

        assertEquals(listOf(jueves, hoyEnLaManana, ayer), escuchados)
    }

    @Test
    fun buscar_noDistingueMayusculasNiEspacios() {
        val encontrados = conversacion.filtrar(FiltroHistorial.TODOS, "  PASAJE ")

        assertEquals(listOf(hoyEnLaManana), encontrados)
    }

    @Test
    fun agruparPorDia_ordenaDelMasNuevoAlMasAntiguo() {
        val grupos = conversacion.agruparPorDia(ahora, utc)

        assertEquals(listOf("Hoy", "Ayer", "Jueves 8 de octubre"), grupos.keys.toList())
        assertEquals(listOf(hoyRespuesta, hoyEnLaManana), grupos["Hoy"])
    }

    @Test
    fun tituloDelDia_cambiaALaMedianoche() {
        // 00:05 del 10 de octubre es "Hoy" y 23:55 del 9 de octubre es "Ayer".
        assertEquals("Hoy", tituloDelDia(1_791_590_700_000L, ahora, utc))
        assertEquals("Ayer", tituloDelDia(1_791_590_100_000L, ahora, utc))
    }

    @Test
    fun resumen_cuentaEscuchadosYEscritos() {
        assertEquals(
            ResumenHistorial(total = 4, escuchados = 3, escritos = 1),
            conversacion.resumen()
        )
    }

    @Test
    fun resumenDeListaVacia_esCero() {
        assertEquals(ResumenHistorial(), emptyList<Mensaje>().resumen())
    }
}
