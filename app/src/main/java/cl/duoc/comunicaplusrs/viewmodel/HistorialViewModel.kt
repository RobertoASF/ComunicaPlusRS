package cl.duoc.comunicaplusrs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.MensajeRepository
import cl.duoc.comunicaplusrs.data.ServiceLocator
import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.utils.FiltroHistorial
import cl.duoc.comunicaplusrs.utils.ResumenHistorial
import cl.duoc.comunicaplusrs.utils.agruparPorDia
import cl.duoc.comunicaplusrs.utils.filtrar
import cl.duoc.comunicaplusrs.utils.mensajeDeError
import cl.duoc.comunicaplusrs.utils.resumen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistorialUiState(
    // Clave: "Hoy", "Ayer" o la fecha. Valor: los mensajes de ese día.
    val grupos: Map<String, List<Mensaje>> = emptyMap(),
    val resumen: ResumenHistorial = ResumenHistorial(),
    val filtro: FiltroHistorial = FiltroHistorial.TODOS,
    val busqueda: String = "",
    val mensaje: String = "",
    val esError: Boolean = false
)

private data class Aviso(
    val texto: String = "",
    val esError: Boolean = false
)

class HistorialViewModel(
    authService: AuthService,
    private val conversacionesRepository: MensajeRepository,
    // Se recibe la hora como función para poder fijarla en las pruebas.
    private val ahora: () -> Long = System::currentTimeMillis
) : ViewModel() {

    private val uid = authService.uidActual

    private val mensajes = MutableStateFlow<List<Mensaje>>(emptyList())
    private val filtro = MutableStateFlow(FiltroHistorial.TODOS)
    private val busqueda = MutableStateFlow("")
    private val aviso = MutableStateFlow(Aviso())

    // combine arma el estado de la pantalla cada vez que cambia cualquiera de los cuatro flujos.
    val uiState: StateFlow<HistorialUiState> = combine(
        mensajes,
        filtro,
        busqueda,
        aviso
    ) { lista, filtroActual, texto, avisoActual ->
        HistorialUiState(
            grupos = lista
                .filtrar(filtroActual, texto)
                .agruparPorDia(ahora()),
            resumen = lista.resumen(),
            filtro = filtroActual,
            busqueda = texto,
            mensaje = avisoActual.texto,
            esError = avisoActual.esError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HistorialUiState()
    )

    init {
        if (uid != null) {
            viewModelScope.launch {
                conversacionesRepository.escuchar(uid)
                    .catch { e -> aviso.value = Aviso(mensajeDeError(e), esError = true) }
                    .collect { mensajes.value = it }
            }
        }
    }

    fun cambiarFiltro(nuevo: FiltroHistorial) {
        filtro.value = nuevo
    }

    fun buscar(texto: String) {
        busqueda.value = texto
    }

    fun eliminar(mensaje: Mensaje) {
        val uid = uid ?: return

        viewModelScope.launch {
            try {
                conversacionesRepository.eliminar(uid, mensaje.id)
                aviso.value = Aviso("Mensaje eliminado.")
            } catch (e: Exception) {
                aviso.value = Aviso(mensajeDeError(e), esError = true)
            }
        }
    }

    fun borrarTodo() {
        val uid = uid ?: return

        viewModelScope.launch {
            try {
                conversacionesRepository.eliminarTodos(uid)
                aviso.value = Aviso("Historial borrado.")
            } catch (e: Exception) {
                aviso.value = Aviso(mensajeDeError(e), esError = true)
            }
        }
    }

    fun mostrarError(texto: String) {
        aviso.value = Aviso(texto, esError = true)
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                HistorialViewModel(
                    ServiceLocator.authService,
                    ServiceLocator.conversacionesRepository
                )
            }
        }
    }
}
