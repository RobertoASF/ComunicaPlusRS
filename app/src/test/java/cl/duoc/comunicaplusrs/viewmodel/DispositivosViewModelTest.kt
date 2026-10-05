package cl.duoc.comunicaplusrs.viewmodel

import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.DispositivoRepository
import cl.duoc.comunicaplusrs.data.ProveedorUbicacion
import cl.duoc.comunicaplusrs.data.Ubicacion
import cl.duoc.comunicaplusrs.model.Dispositivo
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

class DispositivosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Este teléfono está en Santiago y el otro en Valparaíso.
    private val esteTelefono = Dispositivo(
        id = "tel-1", nombre = "Pixel 3a", latitud = -33.4372, longitud = -70.6506
    )
    private val otroTelefono = Dispositivo(
        id = "tel-2", nombre = "Samsung A51", latitud = -33.0472, longitud = -71.6127
    )

    private val repositorio: DispositivoRepository = mock {
        on { escuchar("uid-1") } doReturn flowOf(listOf(esteTelefono, otroTelefono))
    }

    private val proveedorUbicacion: ProveedorUbicacion = mock()

    private fun crearViewModel(uid: String? = "uid-1"): DispositivosViewModel {
        val authService: AuthService = mock {
            on { uidActual } doReturn uid
        }
        return DispositivosViewModel(
            authService = authService,
            repositorio = repositorio,
            proveedorUbicacion = proveedorUbicacion,
            idDispositivo = "tel-1",
            nombreDispositivo = "Pixel 3a"
        )
    }

    @Test
    fun separaEsteTelefonoYCalculaLaDistanciaALosDemas() {
        val viewModel = crearViewModel()
        val estado = viewModel.uiState.value

        assertEquals("tel-1", estado.actual?.id)
        assertEquals(1, estado.otros.size)
        assertEquals("tel-2", estado.otros[0].dispositivo.id)
        assertEquals("99,4 km", estado.otros[0].distancia)
    }

    @Test
    fun alApagarElSwitch_seOcultaLaDistancia() {
        val viewModel = crearViewModel()

        viewModel.cambiarMostrarDistancia(false)

        assertNull(viewModel.uiState.value.otros[0].distancia)
    }

    @Test
    fun actualizarUbicacion_guardaEsteTelefonoConLaNuevaPosicion() = runTest {
        proveedorUbicacion.stub {
            onBlocking { obtenerUbicacion() } doReturn Ubicacion(-33.45, -70.66, 12f)
            onBlocking { obtenerDireccion(any(), any()) } doReturn "Santiago, Chile"
        }
        val viewModel = crearViewModel()

        viewModel.actualizarUbicacion()

        val captor = argumentCaptor<Dispositivo>()
        verify(repositorio).guardar(eq("uid-1"), captor.capture())
        assertEquals("tel-1", captor.firstValue.id)
        assertEquals(-33.45, captor.firstValue.latitud, 0.0001)
        assertEquals("Santiago, Chile", captor.firstValue.direccion)
        assertEquals("Ubicación actualizada.", viewModel.uiState.value.mensaje)
    }

    @Test
    fun sinGps_muestraErrorYNoGuarda() = runTest {
        proveedorUbicacion.stub {
            onBlocking { obtenerUbicacion() } doReturn null
        }
        val viewModel = crearViewModel()

        viewModel.actualizarUbicacion()

        verify(repositorio, never()).guardar(any(), any())
        assertTrue(viewModel.uiState.value.esError)
    }

    @Test
    fun sinSesion_pideIniciarSesion() {
        val viewModel = crearViewModel(uid = null)

        assertEquals(
            "Debes iniciar sesión para buscar tus dispositivos.",
            viewModel.uiState.value.mensaje
        )
    }
}
