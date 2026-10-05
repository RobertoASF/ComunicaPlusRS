package cl.duoc.comunicaplusrs.robolectric

import cl.duoc.comunicaplusrs.utils.mensajeDeError
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Las excepciones de Firebase usan TextUtils de Android al crearse,
// por eso esta prueba necesita Robolectric.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ErroresFirebaseTest {

    @Test
    fun sinInternet_pideRevisarLaConexion() {
        val mensaje = mensajeDeError(FirebaseNetworkException("sin red"))

        assertEquals("No hay conexión a internet. Revisa tu red e intenta nuevamente.", mensaje)
    }

    @Test
    fun correoYaRegistrado() {
        val error = FirebaseAuthUserCollisionException("ERROR_EMAIL_ALREADY_IN_USE", "en uso")

        assertEquals("El correo ya se encuentra registrado.", mensajeDeError(error))
    }

    @Test
    fun passwordDebil_seRevisaAntesQueCredencialesInvalidas() {
        val error = FirebaseAuthWeakPasswordException("ERROR_WEAK_PASSWORD", "debil", "corta")

        assertEquals(
            "La contraseña es muy débil. Usa al menos 6 caracteres.",
            mensajeDeError(error)
        )
    }

    @Test
    fun credencialesIncorrectas() {
        val error = FirebaseAuthInvalidCredentialsException("ERROR_INVALID_CREDENTIAL", "mal")

        assertEquals("Correo o contraseña incorrectos.", mensajeDeError(error))
    }

    @Test
    fun firestoreSinPermiso() {
        val error = FirebaseFirestoreException(
            "denegado",
            FirebaseFirestoreException.Code.PERMISSION_DENIED
        )

        assertEquals("No tienes permiso para acceder a estos datos.", mensajeDeError(error))
    }

    @Test
    fun errorDesconocido_muestraMensajeGeneral() {
        assertEquals(
            "Ocurrió un error inesperado. Intenta nuevamente.",
            mensajeDeError(IllegalStateException())
        )
    }
}
