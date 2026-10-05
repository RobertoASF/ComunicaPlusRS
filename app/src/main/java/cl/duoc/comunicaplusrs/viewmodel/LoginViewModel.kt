package cl.duoc.comunicaplusrs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.ServiceLocator
import cl.duoc.comunicaplusrs.utils.esCorreoValido
import cl.duoc.comunicaplusrs.utils.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val cargando: Boolean = false,
    val mensaje: String = "",
    val esError: Boolean = false,
    val sesionIniciada: Boolean = false
)

class LoginViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun iniciarSesion(correo: String, password: String) {

        val error = when {
            correo.isBlank() -> "Debes ingresar tu correo."
            !correo.trim().esCorreoValido() -> "Ingresa un correo válido."
            password.isBlank() -> "Debes ingresar tu contraseña."
            else -> null
        }

        // Si falta algún dato no se llama a Firebase.
        if (error != null) {
            _uiState.value = LoginUiState(mensaje = error, esError = true)
            return
        }

        _uiState.value = LoginUiState(cargando = true)

        viewModelScope.launch {
            try {
                authService.iniciarSesion(correo.trim(), password)
                _uiState.value = LoginUiState(sesionIniciada = true)
            } catch (e: Exception) {
                _uiState.value = LoginUiState(
                    mensaje = mensajeDeError(e),
                    esError = true
                )
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                LoginViewModel(ServiceLocator.authService)
            }
        }
    }
}
