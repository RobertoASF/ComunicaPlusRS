package cl.duoc.comunicaplusrs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.ServiceLocator
import cl.duoc.comunicaplusrs.data.UsuarioRepository
import cl.duoc.comunicaplusrs.model.Usuario
import cl.duoc.comunicaplusrs.utils.mensajeDeError
import cl.duoc.comunicaplusrs.utils.validarCampo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PerfilUiState(
    val cargando: Boolean = false,
    val mensaje: String = "",
    val esError: Boolean = false,
    val cuentaEliminada: Boolean = false
)

// Este ViewModel vive mientras vive la Activity, así el perfil (nombre y
// preferencias de accesibilidad) queda disponible en todas las pantallas.
@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModel(
    private val authService: AuthService,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val uidSesion = MutableStateFlow(authService.uidActual)

    // Cada vez que cambia la sesión se empieza a escuchar el documento del nuevo usuario.
    val usuario: StateFlow<Usuario?> = uidSesion
        .flatMapLatest { uid ->
            if (uid == null) {
                flowOf(null)
            } else {
                usuarioRepository.escuchar(uid).catch { emit(null) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    val correo: String
        get() = authService.correoActual.orEmpty()

    fun haySesion(): Boolean = authService.uidActual != null

    // Se llama después de iniciar sesión o registrarse.
    fun actualizarSesion() {
        uidSesion.value = authService.uidActual
        _uiState.value = PerfilUiState()
    }

    fun guardarCambios(
        nombre: String,
        tipoComunicacion: String,
        preferenciaInterfaz: String,
        opcionesAccesibilidad: Set<String>
    ) {
        val uid = authService.uidActual ?: return

        if (!validarCampo(nombre) { it.isNotEmpty() }) {
            _uiState.value = PerfilUiState(mensaje = "Debes ingresar tu nombre.", esError = true)
            return
        }

        // Si el perfil no existía (por ejemplo, falló al registrarse) se crea aquí.
        val base = usuario.value ?: Usuario(
            correo = correo,
            aceptaTerminos = true,
            fechaRegistro = System.currentTimeMillis()
        )

        val actualizado = base.copy(
            id = uid,
            nombre = nombre.trim(),
            tipoComunicacion = tipoComunicacion,
            preferenciaInterfaz = preferenciaInterfaz,
            opcionesAccesibilidad = opcionesAccesibilidad.toList()
        )

        _uiState.value = PerfilUiState(cargando = true)

        viewModelScope.launch {
            try {
                usuarioRepository.guardar(actualizado)
                _uiState.value = PerfilUiState(mensaje = "Cambios guardados.")
            } catch (e: Exception) {
                _uiState.value = PerfilUiState(mensaje = mensajeDeError(e), esError = true)
            }
        }
    }

    fun eliminarCuenta(password: String) {
        val uid = authService.uidActual ?: return

        if (password.isBlank()) {
            _uiState.value = PerfilUiState(
                mensaje = "Ingresa tu contraseña para confirmar.",
                esError = true
            )
            return
        }

        _uiState.value = PerfilUiState(cargando = true)

        viewModelScope.launch {
            try {
                // Firebase pide un inicio de sesión reciente para borrar la cuenta.
                authService.reautenticar(password)
                usuarioRepository.eliminar(uid)
                authService.eliminarCuenta()

                uidSesion.value = null
                _uiState.value = PerfilUiState(cuentaEliminada = true)
            } catch (e: Exception) {
                _uiState.value = PerfilUiState(mensaje = mensajeDeError(e), esError = true)
            }
        }
    }

    fun cerrarSesion() {
        uidSesion.value = null
        authService.cerrarSesion()
        _uiState.value = PerfilUiState()
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(mensaje = "")
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                PerfilViewModel(
                    ServiceLocator.authService,
                    ServiceLocator.usuarioRepository
                )
            }
        }
    }
}
