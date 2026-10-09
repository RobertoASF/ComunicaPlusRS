package cl.duoc.comunicaplusrs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.MensajeRepository
import cl.duoc.comunicaplusrs.data.ServiceLocator
import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.utils.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HablarUiState(
    val ultimoTexto: String = "",
    val historial: List<Mensaje> = emptyList(),
    // Cuenta los textos nuevos para disparar la vibración y la alerta visual.
    val textosRecibidos: Int = 0,
    val mensaje: String = "",
    val esError: Boolean = false
)

class HablarViewModel(
    authService: AuthService,
    private val conversacionesRepository: MensajeRepository
) : ViewModel() {

    private val uid = authService.uidActual

    private val _uiState = MutableStateFlow(HablarUiState())
    val uiState: StateFlow<HablarUiState> = _uiState.asStateFlow()

    init {
        if (uid != null) {
            viewModelScope.launch {
                conversacionesRepository.escuchar(uid)
                    .catch { e -> mostrarError(mensajeDeError(e)) }
                    .collect { lista ->
                        _uiState.update { it.copy(historial = lista) }
                    }
            }
        }
    }

    fun textoReconocido(texto: String) {
        if (texto.isBlank()) return

        _uiState.update {
            it.copy(
                ultimoTexto = texto.trim(),
                textosRecibidos = it.textosRecibidos + 1,
                mensaje = ""
            )
        }

        val uid = uid ?: return
        viewModelScope.launch {
            try {
                conversacionesRepository.agregar(uid, texto)
            } catch (e: Exception) {
                mostrarError(mensajeDeError(e))
            }
        }
    }

    fun eliminar(mensaje: Mensaje) {
        val uid = uid ?: return

        viewModelScope.launch {
            try {
                conversacionesRepository.eliminar(uid, mensaje.id)
            } catch (e: Exception) {
                mostrarError(mensajeDeError(e))
            }
        }
    }

    fun borrarHistorial() {
        val uid = uid ?: return

        viewModelScope.launch {
            try {
                conversacionesRepository.eliminarTodos(uid)
                _uiState.update { it.copy(mensaje = "Historial borrado.", esError = false) }
            } catch (e: Exception) {
                mostrarError(mensajeDeError(e))
            }
        }
    }

    fun mostrarError(texto: String) {
        _uiState.update { it.copy(mensaje = texto, esError = true) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                HablarViewModel(
                    ServiceLocator.authService,
                    ServiceLocator.conversacionesRepository
                )
            }
        }
    }
}
