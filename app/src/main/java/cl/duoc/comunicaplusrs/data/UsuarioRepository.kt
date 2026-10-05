package cl.duoc.comunicaplusrs.data

import cl.duoc.comunicaplusrs.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

// CRUD del perfil del usuario en Firestore.
interface UsuarioRepository {

    // Crea el documento o lo reemplaza si ya existe.
    suspend fun guardar(usuario: Usuario)

    fun escuchar(uid: String): Flow<Usuario?>

    // Borra el perfil y todo lo que el usuario guardó.
    suspend fun eliminar(uid: String)
}

class FirestoreUsuarioRepository(
    private val db: FirebaseFirestore
) : UsuarioRepository {

    private fun documento(uid: String) =
        db.collection(COLECCION_USUARIOS).document(uid)

    override suspend fun guardar(usuario: Usuario) {
        withTimeout(TIEMPO_MAXIMO_MS) {
            documento(usuario.id).set(usuario).await()
        }
    }

    // dataObjects() entrega un Flow que se actualiza cada vez que cambia el documento.
    override fun escuchar(uid: String): Flow<Usuario?> {
        return documento(uid).dataObjects<Usuario>()
    }

    override suspend fun eliminar(uid: String) {
        val perfil = documento(uid)

        withTimeout(TIEMPO_MAXIMO_MS) {
            // Firestore no borra las subcolecciones al borrar el documento,
            // por eso se eliminan una por una.
            SUBCOLECCIONES.forEach { nombre ->
                val documentos = perfil.collection(nombre).get().await()
                val batch = db.batch()
                documentos.forEach { batch.delete(it.reference) }
                batch.commit().await()
            }
            perfil.delete().await()
        }
    }

    companion object {
        const val COLECCION_USUARIOS = "usuarios"
        val SUBCOLECCIONES = listOf("frases", "conversaciones", "dispositivos")
    }
}

// Si no hay internet Firestore deja la escritura pendiente y nunca responde,
// así que se corta la espera para avisar al usuario.
const val TIEMPO_MAXIMO_MS = 15_000L
