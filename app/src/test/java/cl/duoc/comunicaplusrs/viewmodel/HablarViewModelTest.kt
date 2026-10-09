package cl.duoc.comunicaplusrs.viewmodel

import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.MensajeRepository
import cl.duoc.comunicaplusrs.model.Mensaje
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class HablarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val conversacion = Mensaje(id = "c1", texto = "Buenos días", fecha = 1L)

    private val authService: AuthService = mock {
        on { uidActual } doReturn "uid-1"
    }

    private val conversacionesRepository: MensajeRepository = mock {
        on { escuchar("uid-1") } doReturn flowOf(listOf(conversacion))
    }

    private lateinit var viewModel: HablarViewModel

    @Before
    fun setUp() {
        viewModel = HablarViewModel(authService, conversacionesRepository)
    }

    @Test
    fun alAbrir_cargaElHistorial() {
        assertEquals(listOf(conversacion), viewModel.uiState.value.historial)
    }

    @Test
    fun textoReconocido_seMuestraYSeGuardaEnElHistorial() = runTest {
        viewModel.textoReconocido("¿Cuánto cuesta el pasaje?")

        assertEquals("¿Cuánto cuesta el pasaje?", viewModel.uiState.value.ultimoTexto)
        assertEquals(1, viewModel.uiState.value.textosRecibidos)
        verify(conversacionesRepository).agregar("uid-1", "¿Cuánto cuesta el pasaje?")
    }

    @Test
    fun textoVacio_seIgnora() = runTest {
        viewModel.textoReconocido("   ")

        assertEquals(0, viewModel.uiState.value.textosRecibidos)
        verify(conversacionesRepository, never()).agregar(any(), any())
    }

    @Test
    fun borrarHistorial_eliminaTodasLasConversaciones() = runTest {
        viewModel.borrarHistorial()

        verify(conversacionesRepository).eliminarTodos("uid-1")
        assertEquals("Historial borrado.", viewModel.uiState.value.mensaje)
    }
}
