package cl.duoc.comunicaplusrs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.data.ServiceLocator
import cl.duoc.comunicaplusrs.data.UsuarioRepository
import cl.duoc.comunicaplusrs.model.Usuario
import cl.duoc.comunicaplusrs.utils.LARGO_MINIMO_PASSWORD
import cl.duoc.comunicaplusrs.utils.esCorreoValido
import cl.duoc.comunicaplusrs.utils.mensajeDeError
import cl.duoc.comunicaplusrs.utils.validarCampo
import cl.duoc.comunicaplusrs.utils.validarPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegistroUiState(
    val cargando: Boolean = false,
    val mensaje: String = "",
    val registroCorrecto: Boolean = false
)

class RegistroViewModel(
    private val authService: AuthService,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun registrar(
        nombre: String,
        correo: String,
        password: String,
        tipoComunicacion: String,
        preferenciaInterfaz: String,
        aceptaTerminos: Boolean,
        opcionesAccesibilidad: Set<String>
    ) {
        // Mismas validaciones de la entrega anterior, usando lambdas.
        val error = when {
            !validarCampo(nombre) { it.isNotEmpty() } -> "Debes ingresar tu nombre."
            !validarCampo(correo) { it.isNotEmpty() } -> "Debes ingresar tu correo."
            !validarCampo(correo) { it.esCorreoValido() } -> "Ingresa un correo válido."
            !validarPassword(password) ->
                "La contraseña debe tener al menos $LARGO_MINIMO_PASSWORD caracteres."
            !aceptaTerminos -> "Debes aceptar los términos y condiciones."
            else -> null
        }

        if (error != null) {
            _uiState.value = RegistroUiState(mensaje = error)
            return
        }

        _uiState.value = RegistroUiState(cargando = true)

        viewModelScope.launch {
            try {
                // Primero se crea la cuenta en Authentication y con el uid
                // se guarda el perfil en Firestore.
                val uid = authService.registrar(correo.trim(), password)

                val usuario = Usuario(
                    id = uid,
                    nombre = nombre.trim(),
                    correo = correo.trim(),
                    tipoComunicacion = tipoComunicacion,
                    preferenciaInterfaz = preferenciaInterfaz,
                    aceptaTerminos = aceptaTerminos,
                    opcionesAccesibilidad = opcionesAccesibilidad.toList(),
                    fechaRegistro = System.currentTimeMillis()
                )
                usuarioRepository.guardar(usuario)

                _uiState.value = RegistroUiState(
                    mensaje = "Usuario registrado correctamente.",
                    registroCorrecto = true
                )
            } catch (e: Exception) {
                _uiState.value = RegistroUiState(mensaje = mensajeDeError(e))
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                RegistroViewModel(
                    ServiceLocator.authService,
                    ServiceLocator.usuarioRepository
                )
            }
        }
    }
}
