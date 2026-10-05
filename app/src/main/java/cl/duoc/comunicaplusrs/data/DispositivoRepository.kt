package cl.duoc.comunicaplusrs.data

import cl.duoc.comunicaplusrs.model.Dispositivo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

// CRUD de los dispositivos del usuario para la pantalla Buscar dispositivo.
interface DispositivoRepository {

    fun escuchar(uid: String): Flow<List<Dispositivo>>

    // El id del documento es el id del teléfono, así cada teléfono
    // tiene un solo registro que se va actualizando.
    suspend fun guardar(uid: String, dispositivo: Dispositivo)

    suspend fun eliminar(uid: String, id: String)
}

class FirestoreDispositivoRepository(
    private val db: FirebaseFirestore
) : DispositivoRepository {

    private fun coleccion(uid: String) = db
        .collection(FirestoreUsuarioRepository.COLECCION_USUARIOS)
        .document(uid)
        .collection(DISPOSITIVOS)

    override fun escuchar(uid: String): Flow<List<Dispositivo>> {
        return coleccion(uid)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .dataObjects<Dispositivo>()
    }

    override suspend fun guardar(uid: String, dispositivo: Dispositivo) {
        withTimeout(TIEMPO_MAXIMO_MS) {
            coleccion(uid).document(dispositivo.id).set(dispositivo).await()
        }
    }

    override suspend fun eliminar(uid: String, id: String) {
        withTimeout(TIEMPO_MAXIMO_MS) {
            coleccion(uid).document(id).delete().await()
        }
    }

    companion object {
        const val DISPOSITIVOS = "dispositivos"
    }
}
