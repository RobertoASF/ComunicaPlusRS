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

data class RecuperarUiState(
    val cargando: Boolean = false,
    val mensaje: String = "",
    val enviado: Boolean = false
)

class RecuperarViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecuperarUiState())
    val uiState: StateFlow<RecuperarUiState> = _uiState.asStateFlow()

    fun enviarCorreo(correo: String) {

        if (correo.isBlank()) {
            _uiState.value = RecuperarUiState(mensaje = "Debe ingresar un correo.")
            return
        }

        if (!correo.trim().esCorreoValido()) {
            _uiState.value = RecuperarUiState(mensaje = "Ingresa un correo válido.")
            return
        }

        _uiState.value = RecuperarUiState(cargando = true)

        viewModelScope.launch {
            try {
                authService.enviarRecuperacion(correo.trim())

                // Firebase no indica si el correo existe (protección contra
                // enumeración de cuentas), por eso el mensaje es general.
                _uiState.value = RecuperarUiState(
                    mensaje = "Si el correo está registrado, te llegará un enlace para crear una nueva contraseña.",
                    enviado = true
                )
            } catch (e: Exception) {
                _uiState.value = RecuperarUiState(mensaje = mensajeDeError(e))
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                RecuperarViewModel(ServiceLocator.authService)
            }
        }
    }
}
