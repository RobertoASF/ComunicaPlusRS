package cl.duoc.comunicaplusrs.viewmodel

import cl.duoc.comunicaplusrs.data.AuthService
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

// Se usa un mock de AuthService para no depender de Firebase ni de internet.
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authService: AuthService = mock()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(authService)
    }

    @Test
    fun correoVacio_muestraErrorYNoLlamaAFirebase() = runTest {
        viewModel.iniciarSesion("", "123456")

        assertEquals("Debes ingresar tu correo.", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.esError)
        verify(authService, never()).iniciarSesion(any(), any())
    }

    @Test
    fun correoMalEscrito_muestraError() = runTest {
        viewModel.iniciarSesion("ana.duoc.cl", "123456")

        assertEquals("Ingresa un correo válido.", viewModel.uiState.value.mensaje)
        verify(authService, never()).iniciarSesion(any(), any())
    }

    @Test
    fun passwordVacia_muestraError() = runTest {
        viewModel.iniciarSesion("ana@duoc.cl", "")

        assertEquals("Debes ingresar tu contraseña.", viewModel.uiState.value.mensaje)
        verify(authService, never()).iniciarSesion(any(), any())
    }

    @Test
    fun datosCorrectos_llamaAFirebaseSinEspaciosYAbreLaSesion() = runTest {
        viewModel.iniciarSesion("  ana@duoc.cl ", "123456")

        verify(authService).iniciarSesion("ana@duoc.cl", "123456")
        assertTrue(viewModel.uiState.value.sesionIniciada)
        assertFalse(viewModel.uiState.value.esError)
    }

    @Test
    fun firebaseRechazaLaClave_muestraMensajeEntendible() = runTest {
        // Se usa un mock de la excepción porque su constructor necesita clases de Android.
        val error = mock<FirebaseAuthInvalidCredentialsException>()
        authService.stub {
            onBlocking { iniciarSesion(any(), any()) } doAnswer { throw error }
        }

        viewModel.iniciarSesion("ana@duoc.cl", "clave-mala")

        assertEquals("Correo o contraseña incorrectos.", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.sesionIniciada)
        assertFalse(viewModel.uiState.value.cargando)
    }
}
