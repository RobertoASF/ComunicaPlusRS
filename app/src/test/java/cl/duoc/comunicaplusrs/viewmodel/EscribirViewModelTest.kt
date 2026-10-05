package cl.duoc.comunicaplusrs.viewmodel

import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.MensajeRepository
import cl.duoc.comunicaplusrs.model.Mensaje
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class EscribirViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fraseGuardada = Mensaje(id = "f1", texto = "Hola, soy sordo", fecha = 1L)

    private val authService: AuthService = mock {
        on { uidActual } doReturn "uid-1"
    }

    private val frasesRepository: MensajeRepository = mock {
        on { escuchar("uid-1") } doReturn flowOf(listOf(fraseGuardada))
    }

    private lateinit var viewModel: EscribirViewModel

    @Before
    fun setUp() {
        viewModel = EscribirViewModel(authService, frasesRepository)
    }

    @Test
    fun alAbrir_cargaLasFrasesDelUsuario() {
        assertEquals(listOf(fraseGuardada), viewModel.uiState.value.frases)
    }

    @Test
    fun guardarFraseNueva_laAgregaEnFirestore() = runTest {
        viewModel.guardarFrase("Necesito ayuda")

        verify(frasesRepository).agregar("uid-1", "Necesito ayuda")
        assertEquals(1, viewModel.uiState.value.guardadosOk)
        assertEquals("Frase guardada.", viewModel.uiState.value.mensaje)
    }

    @Test
    fun fraseVacia_noSeGuarda() = runTest {
        viewModel.guardarFrase("   ")

        verify(frasesRepository, never()).agregar(any(), any())
        assertEquals(0, viewModel.uiState.value.guardadosOk)
    }

    @Test
    fun editarFrase_actualizaElMismoDocumento() = runTest {
        viewModel.editar(fraseGuardada)
        viewModel.guardarFrase("Hola, soy una persona sorda")

        verify(frasesRepository).actualizar("uid-1", "f1", "Hola, soy una persona sorda")
        verify(frasesRepository, never()).agregar(any(), any())
        assertNull(viewModel.uiState.value.idEditando)
    }

    @Test
    fun eliminarFrase_laBorraDeFirestore() = runTest {
        viewModel.eliminar(fraseGuardada)

        verify(frasesRepository).eliminar("uid-1", "f1")
        assertEquals("Frase eliminada.", viewModel.uiState.value.mensaje)
    }
}
