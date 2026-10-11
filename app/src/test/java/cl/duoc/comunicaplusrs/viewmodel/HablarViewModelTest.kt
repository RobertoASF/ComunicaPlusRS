package cl.duoc.comunicaplusrs.viewmodel

import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.MensajeRepository
import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.model.ORIGEN_ESCRITO
import cl.duoc.comunicaplusrs.model.ORIGEN_ESCUCHADO
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

    // Firestore los entrega del más nuevo al más antiguo.
    private val conversacion = (6 downTo 1).map { numero ->
        Mensaje(id = "c$numero", texto = "Mensaje $numero", fecha = numero.toLong())
    }

    private val authService: AuthService = mock {
        on { uidActual } doReturn "uid-1"
    }

    private val conversacionesRepository: MensajeRepository = mock {
        on { escuchar("uid-1") } doReturn flowOf(conversacion)
    }

    private lateinit var viewModel: HablarViewModel

    @Before
    fun setUp() {
        viewModel = HablarViewModel(authService, conversacionesRepository)
    }

    @Test
    fun alAbrir_muestraLosUltimosCuatroMensajesComoChat() {
        val recientes = viewModel.uiState.value.recientes

        assertEquals(4, recientes.size)
        // El más nuevo queda abajo, como en una app de mensajería.
        assertEquals(listOf("c3", "c4", "c5", "c6"), recientes.map { it.id })
    }

    @Test
    fun textoReconocido_seMuestraYSeGuardaComoEscuchado() = runTest {
        viewModel.textoReconocido("¿Cuánto cuesta el pasaje?")

        assertEquals("¿Cuánto cuesta el pasaje?", viewModel.uiState.value.ultimoTexto)
        assertEquals(1, viewModel.uiState.value.textosRecibidos)
        verify(conversacionesRepository).agregar("uid-1", "¿Cuánto cuesta el pasaje?", ORIGEN_ESCUCHADO)
    }

    @Test
    fun textoVacio_seIgnora() = runTest {
        viewModel.textoReconocido("   ")

        assertEquals(0, viewModel.uiState.value.textosRecibidos)
        verify(conversacionesRepository, never()).agregar(any(), any(), any())
    }

    @Test
    fun responder_guardaLaRespuestaComoEscrita() = runTest {
        viewModel.responder("Son mil pesos")

        verify(conversacionesRepository).agregar("uid-1", "Son mil pesos", ORIGEN_ESCRITO)
        assertEquals(1, viewModel.uiState.value.respuestasOk)
    }

    @Test
    fun respuestaVacia_muestraErrorYNoSeGuarda() = runTest {
        viewModel.responder("")

        verify(conversacionesRepository, never()).agregar(any(), any(), any())
        assertEquals(0, viewModel.uiState.value.respuestasOk)
        assertTrue(viewModel.uiState.value.esError)
    }
}
