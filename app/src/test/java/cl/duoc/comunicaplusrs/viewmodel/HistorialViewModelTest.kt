package cl.duoc.comunicaplusrs.viewmodel

import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.MensajeRepository
import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.model.ORIGEN_ESCRITO
import cl.duoc.comunicaplusrs.model.ORIGEN_ESCUCHADO
import cl.duoc.comunicaplusrs.utils.FiltroHistorial
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class HistorialViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Todos del mismo día para que queden en un solo grupo.
    private val ahora = 1_791_644_400_000L
    private val pregunta = Mensaje(id = "p", texto = "¿En qué le ayudo?", fecha = ahora - 60_000, origen = ORIGEN_ESCUCHADO)
    private val respuesta = Mensaje(id = "r", texto = "Busco la farmacia", fecha = ahora - 30_000, origen = ORIGEN_ESCRITO)

    private val authService: AuthService = mock {
        on { uidActual } doReturn "uid-1"
    }

    private val conversacionesRepository: MensajeRepository = mock {
        on { escuchar("uid-1") } doReturn flowOf(listOf(respuesta, pregunta))
    }

    private lateinit var viewModel: HistorialViewModel

    @Before
    fun setUp() {
        viewModel = HistorialViewModel(authService, conversacionesRepository) { ahora }
    }

    @Test
    fun alAbrir_agrupaLaConversacionDeHoy() {
        val estado = viewModel.uiState.value

        assertEquals(listOf("Hoy"), estado.grupos.keys.toList())
        assertEquals(listOf(respuesta, pregunta), estado.grupos["Hoy"])
        assertEquals(2, estado.resumen.total)
    }

    @Test
    fun filtrarEscuchados_ocultaLasRespuestas() {
        viewModel.cambiarFiltro(FiltroHistorial.ESCUCHADOS)

        val estado = viewModel.uiState.value
        assertEquals(FiltroHistorial.ESCUCHADOS, estado.filtro)
        assertEquals(listOf(pregunta), estado.grupos["Hoy"])
        // El resumen siempre cuenta toda la conversación.
        assertEquals(1, estado.resumen.escritos)
    }

    @Test
    fun buscarSinResultados_dejaLosGruposVacios() {
        viewModel.buscar("hospital")

        assertTrue(viewModel.uiState.value.grupos.isEmpty())
        assertEquals("hospital", viewModel.uiState.value.busqueda)
    }

    @Test
    fun eliminar_borraElMensajeEnFirestore() = runTest {
        viewModel.eliminar(pregunta)

        verify(conversacionesRepository).eliminar("uid-1", "p")
        assertEquals("Mensaje eliminado.", viewModel.uiState.value.mensaje)
    }

    @Test
    fun borrarTodo_eliminaLaConversacionCompleta() = runTest {
        viewModel.borrarTodo()

        verify(conversacionesRepository).eliminarTodos("uid-1")
        assertEquals("Historial borrado.", viewModel.uiState.value.mensaje)
    }
}
