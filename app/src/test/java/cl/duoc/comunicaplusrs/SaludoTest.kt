package cl.duoc.comunicaplusrs

import cl.duoc.comunicaplusrs.utils.armarSaludo
import cl.duoc.comunicaplusrs.utils.primerNombre
import cl.duoc.comunicaplusrs.utils.saludoSegunHora
import org.junit.Assert.assertEquals
import org.junit.Test

class SaludoTest {

    @Test
    fun saludoSegunHora_cubreTodoElDia() {
        assertEquals("Buenos días", saludoSegunHora(6))
        assertEquals("Buenos días", saludoSegunHora(11))
        assertEquals("Buenas tardes", saludoSegunHora(12))
        assertEquals("Buenas tardes", saludoSegunHora(19))
        assertEquals("Buenas noches", saludoSegunHora(20))
        assertEquals("Buenas noches", saludoSegunHora(0))
        assertEquals("Buenas noches", saludoSegunHora(5))
    }

    @Test
    fun primerNombre_ignoraEspaciosYApellidos() {
        assertEquals("Roberto", primerNombre("  Roberto Sanchez  "))
        assertEquals("Ana", primerNombre("Ana"))
        assertEquals("", primerNombre("   "))
    }

    @Test
    fun armarSaludo_usaElNombreDelUsuario() {
        assertEquals("Buenas tardes, Roberto", armarSaludo(15, "Roberto Sanchez"))
    }

    @Test
    fun armarSaludo_sinNombre_daLaBienvenida() {
        assertEquals("Buenos días, te damos la bienvenida", armarSaludo(9, null))
    }
}
