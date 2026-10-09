package cl.duoc.comunicaplusrs.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.DispositivoRepository
import cl.duoc.comunicaplusrs.data.GpsUbicacionService
import cl.duoc.comunicaplusrs.data.PreferenciasLocales
import cl.duoc.comunicaplusrs.data.ProveedorUbicacion
import cl.duoc.comunicaplusrs.data.ServiceLocator
import cl.duoc.comunicaplusrs.model.Dispositivo
import cl.duoc.comunicaplusrs.utils.calcularDistanciaMetros
import cl.duoc.comunicaplusrs.utils.formatearDistancia
import cl.duoc.comunicaplusrs.utils.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ItemDispositivo(
    val dispositivo: Dispositivo,
    val distancia: String?
)

data class DispositivosUiState(
    val nombreDispositivo: String = "",
    val actual: Dispositivo? = null,
    val otros: List<ItemDispositivo> = emptyList(),
    val mostrarDistancia: Boolean = true,
    val cargando: Boolean = false,
    val mensaje: String = "",
    val esError: Boolean = false
)

class DispositivosViewModel(
    private val authService: AuthService,
    private val repositorio: DispositivoRepository,
    private val proveedorUbicacion: ProveedorUbicacion,
    private val idDispositivo: String,
    private val nombreDispositivo: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(DispositivosUiState(nombreDispositivo = nombreDispositivo))
    val uiState: StateFlow<DispositivosUiState> = _uiState.asStateFlow()

    private var dispositivos: List<Dispositivo> = emptyList()

    init {
        val uid = authService.uidActual

        if (uid == null) {
            mostrarError("Debes iniciar sesión para buscar tus dispositivos.")
        } else {
            viewModelScope.launch {
                repositorio.escuchar(uid)
                    .catch { e -> mostrarError(mensajeDeError(e)) }
                    .collect { lista ->
                        dispositivos = lista
                        armarLista()
                    }
            }
        }
    }

    // Separa este teléfono del resto y calcula la distancia a cada uno.
    private fun armarLista() {
        val actual = dispositivos.find { it.id == idDispositivo }
        val mostrarDistancia = _uiState.value.mostrarDistancia

        val otros = dispositivos
            .filter { it.id != idDispositivo }
            .map { otro ->
                val distancia = if (mostrarDistancia && actual != null) {
                    formatearDistancia(
                        calcularDistanciaMetros(
                            actual.latitud, actual.longitud,
                            otro.latitud, otro.longitud
                        )
                    )
                } else {
                    null
                }
                ItemDispositivo(otro, distancia)
            }

        _uiState.update { it.copy(actual = actual, otros = otros) }
    }

    fun cambiarMostrarDistancia(activo: Boolean) {
        _uiState.update { it.copy(mostrarDistancia = activo) }
        armarLista()
    }

    fun actualizarUbicacion() {
        val uid = authService.uidActual ?: return

        _uiState.update { it.copy(cargando = true, mensaje = "") }

        viewModelScope.launch {
            try {
                val ubicacion = proveedorUbicacion.obtenerUbicacion()

                if (ubicacion == null) {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensaje = "No se pudo obtener la ubicación. Revisa que el GPS esté activado.",
                            esError = true
                        )
                    }
                    return@launch
                }

                val direccion = proveedorUbicacion.obtenerDireccion(
                    ubicacion.latitud,
                    ubicacion.longitud
                )

                repositorio.guardar(
                    uid,
                    Dispositivo(
                        id = idDispositivo,
                        nombre = nombreDispositivo,
                        latitud = ubicacion.latitud,
                        longitud = ubicacion.longitud,
                        precision = ubicacion.precision,
                        direccion = direccion,
                        fecha = System.currentTimeMillis()
                    )
                )

                _uiState.update {
                    it.copy(cargando = false, mensaje = "Ubicación actualizada.", esError = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(cargando = false, mensaje = mensajeDeError(e), esError = true)
                }
            }
        }
    }

    fun eliminar(dispositivo: Dispositivo) {
        val uid = authService.uidActual ?: return

        viewModelScope.launch {
            try {
                repositorio.eliminar(uid, dispositivo.id)
                _uiState.update { it.copy(mensaje = "Dispositivo eliminado.", esError = false) }
            } catch (e: Exception) {
                mostrarError(mensajeDeError(e))
            }
        }
    }

    fun mostrarError(texto: String) {
        _uiState.update { it.copy(cargando = false, mensaje = texto, esError = true) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])

                DispositivosViewModel(
                    authService = ServiceLocator.authService,
                    repositorio = ServiceLocator.dispositivoRepository,
                    proveedorUbicacion = GpsUbicacionService(app),
                    idDispositivo = PreferenciasLocales(app).idDispositivo(),
                    nombreDispositivo = nombreDelTelefono()
                )
            }
        }

        private fun nombreDelTelefono(): String {
            val marca = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
            return "$marca ${Build.MODEL}"
        }
    }
}
