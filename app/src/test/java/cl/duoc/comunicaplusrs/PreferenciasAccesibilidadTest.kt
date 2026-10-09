package cl.duoc.comunicaplusrs

import cl.duoc.comunicaplusrs.model.PreferenciasAccesibilidad
import cl.duoc.comunicaplusrs.model.Usuario
import cl.duoc.comunicaplusrs.model.preferencias
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferenciasAccesibilidadTest {

    @Test
    fun usuarioSinOpciones_noActivaNinguna() {
        val usuario = Usuario(nombre = "Ana")

        assertEquals(PreferenciasAccesibilidad(), usuario.preferencias())
    }

    @Test
    fun preferenciaDeInterfazTextoGrande_activaTextoGrande() {
        val usuario = Usuario(preferenciaInterfaz = "Texto grande")

        assertTrue(usuario.preferencias().textoGrande)
    }

    @Test
    fun opcionesMarcadasEnElRegistro_seConviertenEnPreferencias() {
        val usuario = Usuario(
            opcionesAccesibilidad = listOf("Alto contraste", "Vibración", "Alertas visuales")
        )

        val esperado = PreferenciasAccesibilidad(
            textoGrande = false,
            altoContraste = true,
            alertasVisuales = true,
            vibracion = true
        )
        assertEquals(esperado, usuario.preferencias())
    }
}
