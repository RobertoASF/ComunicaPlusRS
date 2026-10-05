package cl.duoc.comunicaplusrs

import cl.duoc.comunicaplusrs.utils.esCorreoValido
import cl.duoc.comunicaplusrs.utils.validarCampo
import cl.duoc.comunicaplusrs.utils.validarMensaje
import cl.duoc.comunicaplusrs.utils.validarPassword
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun correoConFormatoCorrecto_esValido() {
        assertTrue("ana@duoc.cl".esCorreoValido())
    }

    @Test
    fun correoSinArroba_noEsValido() {
        assertFalse("anaduoc.cl".esCorreoValido())
    }

    @Test
    fun correoSinPuntoEnElDominio_noEsValido() {
        assertFalse("ana@duoc".esCorreoValido())
    }

    @Test
    fun correoSinUsuarioAntesDeLaArroba_noEsValido() {
        assertFalse("@duoc.cl".esCorreoValido())
    }

    @Test
    fun passwordDeCincoCaracteres_noEsValida() {
        assertFalse(validarPassword("12345"))
    }

    @Test
    fun passwordDeSeisCaracteres_esValida() {
        assertTrue(validarPassword("123456"))
    }

    @Test
    fun validarCampo_aplicaLaLambdaAlTextoSinEspacios() {
        assertTrue(validarCampo("  hola  ") { it == "hola" })
        assertFalse(validarCampo("    ") { it.isNotEmpty() })
    }

    @Test
    fun validarMensaje_rechazaTextoVacioOMuyLargo() {
        assertFalse(validarMensaje("   "))
        assertFalse(validarMensaje("a".repeat(201)))
        assertTrue(validarMensaje("Necesito ayuda"))
        assertTrue(validarMensaje("a".repeat(200)))
    }
}
