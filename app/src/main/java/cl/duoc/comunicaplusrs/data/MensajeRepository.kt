package cl.duoc.comunicaplusrs.data

import cl.duoc.comunicaplusrs.model.Mensaje
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

// CRUD de textos del usuario. La misma clase sirve para las frases de
// Escribir y para el historial de Hablar, solo cambia la subcolección.
interface MensajeRepository {

    fun escuchar(uid: String): Flow<List<Mensaje>>

    suspend fun agregar(uid: String, texto: String)

    suspend fun actualizar(uid: String, id: String, texto: String)

    suspend fun eliminar(uid: String, id: String)

    suspend fun eliminarTodos(uid: String)
}

class FirestoreMensajeRepository(
    private val db: FirebaseFirestore,
    private val subcoleccion: String
) : MensajeRepository {

    private fun coleccion(uid: String) = db
        .collection(FirestoreUsuarioRepository.COLECCION_USUARIOS)
        .document(uid)
        .collection(subcoleccion)

    override fun escuchar(uid: String): Flow<List<Mensaje>> {
        return coleccion(uid)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .dataObjects<Mensaje>()
    }

    override suspend fun agregar(uid: String, texto: String) {
        val mensaje = Mensaje(
            texto = texto.trim(),
            fecha = System.currentTimeMillis()
        )
        withTimeout(TIEMPO_MAXIMO_MS) {
            coleccion(uid).add(mensaje).await()
        }
    }

    override suspend fun actualizar(uid: String, id: String, texto: String) {
        val cambios = mapOf(
            "texto" to texto.trim(),
            "fecha" to System.currentTimeMillis()
        )
        withTimeout(TIEMPO_MAXIMO_MS) {
            coleccion(uid).document(id).update(cambios).await()
        }
    }

    override suspend fun eliminar(uid: String, id: String) {
        withTimeout(TIEMPO_MAXIMO_MS) {
            coleccion(uid).document(id).delete().await()
        }
    }

    override suspend fun eliminarTodos(uid: String) {
        withTimeout(TIEMPO_MAXIMO_MS) {
            val documentos = coleccion(uid).get().await()
            val batch = db.batch()
            documentos.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
    }

    companion object {
        const val FRASES = "frases"
        const val CONVERSACIONES = "conversaciones"
    }
}
