package cl.duoc.comunicaplusrs.data

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

// Operaciones de autenticación que usan las pantallas.
// Se deja como interfaz para poder reemplazarla por un mock en las pruebas.
interface AuthService {

    val uidActual: String?

    val correoActual: String?

    suspend fun iniciarSesion(correo: String, password: String)

    // Devuelve el uid del usuario creado.
    suspend fun registrar(correo: String, password: String): String

    suspend fun enviarRecuperacion(correo: String)

    suspend fun reautenticar(password: String)

    suspend fun eliminarCuenta()

    fun cerrarSesion()
}

class FirebaseAuthService(
    private val auth: FirebaseAuth
) : AuthService {

    override val uidActual: String?
        get() = auth.currentUser?.uid

    override val correoActual: String?
        get() = auth.currentUser?.email

    override suspend fun iniciarSesion(correo: String, password: String) {
        auth.signInWithEmailAndPassword(correo.trim(), password).await()
    }

    override suspend fun registrar(correo: String, password: String): String {
        val resultado = auth
            .createUserWithEmailAndPassword(correo.trim(), password)
            .await()

        return resultado.user?.uid
            ?: throw IllegalStateException("No se pudo crear el usuario")
    }

    override suspend fun enviarRecuperacion(correo: String) {
        // El correo de recuperación llega en español.
        auth.setLanguageCode("es")
        auth.sendPasswordResetEmail(correo.trim()).await()
    }

    override suspend fun reautenticar(password: String) {
        val usuario = auth.currentUser
            ?: throw IllegalStateException("No hay una sesión iniciada")

        val credencial = EmailAuthProvider.getCredential(
            usuario.email.orEmpty(),
            password
        )
        usuario.reauthenticate(credencial).await()
    }

    override suspend fun eliminarCuenta() {
        auth.currentUser?.delete()?.await()
    }

    override fun cerrarSesion() {
        auth.signOut()
    }
}
