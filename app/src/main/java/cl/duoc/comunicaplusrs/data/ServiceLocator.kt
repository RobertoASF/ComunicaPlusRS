package cl.duoc.comunicaplusrs.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

// Punto único donde se crean los servicios de Firebase.
// Con "by lazy" se crean la primera vez que se usan y luego se reutilizan.
object ServiceLocator {

    val authService: AuthService by lazy {
        FirebaseAuthService(Firebase.auth)
    }

    val usuarioRepository: UsuarioRepository by lazy {
        FirestoreUsuarioRepository(Firebase.firestore)
    }

    val frasesRepository: MensajeRepository by lazy {
        FirestoreMensajeRepository(Firebase.firestore, FirestoreMensajeRepository.FRASES)
    }

    val conversacionesRepository: MensajeRepository by lazy {
        FirestoreMensajeRepository(Firebase.firestore, FirestoreMensajeRepository.CONVERSACIONES)
    }

    val dispositivoRepository: DispositivoRepository by lazy {
        FirestoreDispositivoRepository(Firebase.firestore)
    }
}
