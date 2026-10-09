package cl.duoc.comunicaplusrs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.MensajeRepository
import cl.duoc.comunicaplusrs.data.ServiceLocator
import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.utils.LARGO_MAXIMO_MENSAJE
import cl.duoc.comunicaplusrs.utils.mensajeDeError
import cl.duoc.comunicaplusrs.utils.validarMensaje
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EscribirUiState(
    val frases: List<Mensaje> = emptyList(),
    val idEditando: String? = null,
    val guardando: Boolean = false,
    // Aumenta cada vez que se guarda bien una frase, la pantalla lo usa para limpiar el campo.
    val guardadosOk: Int = 0,
    val mensaje: String = "",
    val esError: Boolean = false
)

class EscribirViewModel(
    authService: AuthService,
    private val frasesRepository: MensajeRepository
) : ViewModel() {

    private val uid = authService.uidActual

    private val _uiState = MutableStateFlow(EscribirUiState())
    val uiState: StateFlow<EscribirUiState> = _uiState.asStateFlow()

    init {
        if (uid != null) {
            viewModelScope.launch {
                frasesRepository.escuchar(uid)
                    .catch { e -> mostrarMensaje(mensajeDeError(e), esError = true) }
                    .collect { lista ->
                        _uiState.update { it.copy(frases = lista) }
                    }
            }
        }
    }

    fun guardarFrase(texto: String) {
        val uid = uid ?: return

        if (!validarMensaje(texto)) {
            mostrarMensaje(
                "Escribe una frase de hasta $LARGO_MAXIMO_MENSAJE caracteres.",
                esError = true
            )
            return
        }

        val idEditando = _uiState.value.idEditando
        _uiState.update { it.copy(guardando = true, mensaje = "") }

        viewModelScope.launch {
            try {
                if (idEditando == null) {
                    frasesRepository.agregar(uid, texto)
                } else {
                    frasesRepository.actualizar(uid, idEditando, texto)
                }

                _uiState.update {
                    it.copy(
                        guardando = false,
                        idEditando = null,
                        guardadosOk = it.guardadosOk + 1,
                        mensaje = if (idEditando == null) "Frase guardada." else "Frase actualizada.",
                        esError = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(guardando = false, mensaje = mensajeDeError(e), esError = true)
                }
            }
        }
    }

    fun editar(frase: Mensaje) {
        _uiState.update { it.copy(idEditando = frase.id, mensaje = "") }
    }

    fun cancelarEdicion() {
        _uiState.update { it.copy(idEditando = null, mensaje = "") }
    }

    fun eliminar(frase: Mensaje) {
        val uid = uid ?: return

        viewModelScope.launch {
            try {
                frasesRepository.eliminar(uid, frase.id)
                mostrarMensaje("Frase eliminada.", esError = false)
            } catch (e: Exception) {
                mostrarMensaje(mensajeDeError(e), esError = true)
            }
        }
    }

    fun mostrarMensaje(texto: String, esError: Boolean) {
        _uiState.update { it.copy(mensaje = texto, esError = esError) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                EscribirViewModel(
                    ServiceLocator.authService,
                    ServiceLocator.frasesRepository
                )
            }
        }
    }
}
