package cl.duoc.comunicaplusrs

import cl.duoc.comunicaplusrs.model.PASOS_TUTORIAL
import cl.duoc.comunicaplusrs.model.SECCION_INICIO
import cl.duoc.comunicaplusrs.model.indiceDeSeccion
import cl.duoc.comunicaplusrs.navigation.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TutorialTest {

    @Test
    fun cadaPantallaConBotonDeAyudaTieneSuPagina() {
        val secciones = PASOS_TUTORIAL.map { it.seccion }

        listOf(
            Routes.ESCRIBIR,
            Routes.HABLAR,
            Routes.HISTORIAL,
            Routes.BUSCAR_DISPOSITIVO,
            Routes.PERFIL
        ).forEach { ruta ->
            assertTrue("Falta la ayuda de $ruta", ruta in secciones)
        }
    }

    @Test
    fun lasSeccionesNoSeRepiten() {
        val secciones = PASOS_TUTORIAL.map { it.seccion }

        assertEquals(secciones.size, secciones.toSet().size)
    }

    @Test
    fun indiceDeSeccion_encuentraLaPagina() {
        assertEquals(0, indiceDeSeccion(SECCION_INICIO))
        assertEquals(2, indiceDeSeccion(Routes.HABLAR))
    }

    @Test
    fun indiceDeSeccion_desconocidaONula_partenEnLaPrimera() {
        assertEquals(0, indiceDeSeccion("no_existe"))
        assertEquals(0, indiceDeSeccion(null))
    }

    @Test
    fun todasLasPaginasTienenConsejos() {
        assertTrue(PASOS_TUTORIAL.all { it.consejos.isNotEmpty() && it.titulo.isNotBlank() })
    }
}
