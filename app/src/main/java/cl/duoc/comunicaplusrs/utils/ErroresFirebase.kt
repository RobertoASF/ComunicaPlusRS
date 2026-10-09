package cl.duoc.comunicaplusrs.utils

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.TimeoutCancellationException

// Cambia los errores de Firebase por mensajes que el usuario pueda entender.
// El orden importa: WeakPassword hereda de InvalidCredentials.
fun mensajeDeError(error: Throwable): String {
    return when (error) {
        is FirebaseNetworkException ->
            "No hay conexión a internet. Revisa tu red e intenta nuevamente."

        is TimeoutCancellationException ->
            "La operación tardó demasiado. Revisa tu conexión a internet."

        is FirebaseAuthUserCollisionException ->
            "El correo ya se encuentra registrado."

        is FirebaseAuthWeakPasswordException ->
            "La contraseña es muy débil. Usa al menos $LARGO_MINIMO_PASSWORD caracteres."

        is FirebaseAuthInvalidUserException ->
            "No existe una cuenta activa con ese correo."

        is FirebaseAuthInvalidCredentialsException ->
            "Correo o contraseña incorrectos."

        is FirebaseAuthRecentLoginRequiredException ->
            "Por seguridad debes ingresar tu contraseña nuevamente."

        is FirebaseTooManyRequestsException ->
            "Demasiados intentos. Espera un momento e intenta de nuevo."

        is FirebaseFirestoreException -> when (error.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "No tienes permiso para acceder a estos datos."

            FirebaseFirestoreException.Code.UNAVAILABLE ->
                "El servicio no está disponible. Revisa tu conexión a internet."

            else -> "Ocurrió un error con la base de datos."
        }

        else -> "Ocurrió un error inesperado. Intenta nuevamente."
    }
}
