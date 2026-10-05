package cl.duoc.comunicaplusrs.viewmodel

import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.UsuarioRepository
import cl.duoc.comunicaplusrs.model.Usuario
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

class RegistroViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authService: AuthService = mock()
    private val usuarioRepository: UsuarioRepository = mock()

    private lateinit var viewModel: RegistroViewModel

    @Before
    fun setUp() {
        viewModel = RegistroViewModel(authService, usuarioRepository)
    }

    private fun registrar(
        nombre: String = "Ana Pérez",
        correo: String = "ana@duoc.cl",
        password: String = "123456",
        aceptaTerminos: Boolean = true
    ) {
        viewModel.registrar(
            nombre = nombre,
            correo = correo,
            password = password,
            tipoComunicacion = "Lengua de señas",
            preferenciaInterfaz = "Texto grande",
            aceptaTerminos = aceptaTerminos,
            opcionesAccesibilidad = setOf("Vibración", "Alto contraste")
        )
    }

    @Test
    fun datosValidos_creaLaCuentaYGuardaElPerfilConElUid() = runTest {
        authService.stub {
            onBlocking { registrar(any(), any()) } doReturn "uid-123"
        }

        registrar()

        val captor = argumentCaptor<Usuario>()
        verify(usuarioRepository).guardar(captor.capture())

        val guardado = captor.firstValue
        assertEquals("uid-123", guardado.id)
        assertEquals("Ana Pérez", guardado.nombre)
        assertEquals("Lengua de señas", guardado.tipoComunicacion)
        assertTrue(guardado.opcionesAccesibilidad.contains("Vibración"))
        assertTrue(viewModel.uiState.value.registroCorrecto)
    }

    @Test
    fun sinAceptarTerminos_noCreaLaCuenta() = runTest {
        registrar(aceptaTerminos = false)

        assertEquals("Debes aceptar los términos y condiciones.", viewModel.uiState.value.mensaje)
        verify(authService, never()).registrar(any(), any())
    }

    @Test
    fun passwordCorta_noCreaLaCuenta() = runTest {
        registrar(password = "1234")

        assertEquals(
            "La contraseña debe tener al menos 6 caracteres.",
            viewModel.uiState.value.mensaje
        )
        verify(authService, never()).registrar(any(), any())
    }

    @Test
    fun nombreConSoloEspacios_noCreaLaCuenta() = runTest {
        registrar(nombre = "   ")

        assertEquals("Debes ingresar tu nombre.", viewModel.uiState.value.mensaje)
        verify(authService, never()).registrar(any(), any())
    }

    @Test
    fun correoYaRegistrado_noGuardaPerfil() = runTest {
        val error = mock<FirebaseAuthUserCollisionException>()
        authService.stub {
            onBlocking { registrar(any(), any()) } doAnswer { throw error }
        }

        registrar()

        assertEquals("El correo ya se encuentra registrado.", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.registroCorrecto)
        verify(usuarioRepository, never()).guardar(any())
    }
}
