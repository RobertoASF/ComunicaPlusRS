package cl.duoc.comunicaplusrs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.MensajeRepository
import cl.duoc.comunicaplusrs.data.ServiceLocator
import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.model.ORIGEN_ESCRITO
import cl.duoc.comunicaplusrs.model.ORIGEN_ESCUCHADO
import cl.duoc.comunicaplusrs.utils.LARGO_MAXIMO_MENSAJE
import cl.duoc.comunicaplusrs.utils.mensajeDeError
import cl.duoc.comunicaplusrs.utils.validarMensaje
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// En Hablar solo se muestran los últimos mensajes; el resto está en Historial.
private const val MENSAJES_RECIENTES = 4

data class HablarUiState(
    val ultimoTexto: String = "",
    val recientes: List<Mensaje> = emptyList(),
    // Cuenta los textos nuevos para disparar la vibración y la alerta visual.
    val textosRecibidos: Int = 0,
    // Aumenta cada vez que se guarda una respuesta, la pantalla lo usa para limpiar el campo.
    val respuestasOk: Int = 0,
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
                        // La lista llega ordenada del más nuevo al más antiguo;
                        // se invierte para leerla de arriba hacia abajo como un chat.
                        _uiState.update {
                            it.copy(recientes = lista.take(MENSAJES_RECIENTES).reversed())
                        }
                    }
            }
        }
    }

    // Solicitud: lo que dijo la otra persona.
    fun textoReconocido(texto: String) {
        if (texto.isBlank()) return

        _uiState.update {
            it.copy(
                ultimoTexto = texto.trim(),
                textosRecibidos = it.textosRecibidos + 1,
                mensaje = ""
            )
        }

        guardar(texto, ORIGEN_ESCUCHADO)
    }

    // Respuesta: lo que escribió el usuario y la app leyó en voz alta.
    fun responder(texto: String) {
        if (!validarMensaje(texto)) {
            mostrarError("Escribe una respuesta de hasta $LARGO_MAXIMO_MENSAJE caracteres.")
            return
        }

        _uiState.update {
            it.copy(respuestasOk = it.respuestasOk + 1, mensaje = "")
        }

        guardar(texto, ORIGEN_ESCRITO)
    }

    private fun guardar(texto: String, origen: String) {
        val uid = uid ?: return

        viewModelScope.launch {
            try {
                conversacionesRepository.agregar(uid, texto.trim(), origen)
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
