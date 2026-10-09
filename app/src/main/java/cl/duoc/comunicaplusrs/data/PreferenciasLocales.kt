package cl.duoc.comunicaplusrs.data

import android.content.Context
import androidx.core.content.edit
import java.util.UUID

// Datos que solo sirven en este teléfono, por eso quedan en SharedPreferences
// y no en Firestore.
class PreferenciasLocales(context: Context) {

    private val prefs = context.getSharedPreferences(
        ARCHIVO,
        Context.MODE_PRIVATE
    )

    // Se genera una sola vez para identificar el teléfono en "dispositivos".
    fun idDispositivo(): String {
        val guardado = prefs.getString(CLAVE_ID_DISPOSITIVO, null)
        if (guardado != null) return guardado

        val nuevo = UUID.randomUUID().toString()
        prefs.edit {
            putString(CLAVE_ID_DISPOSITIVO, nuevo)
        }
        return nuevo
    }

    var tamanoTextoHablar: Float
        get() = prefs.getFloat(CLAVE_TAMANO_TEXTO, TAMANO_TEXTO_INICIAL)
        set(valor) = prefs.edit {
            putFloat(CLAVE_TAMANO_TEXTO, valor)
        }

    companion object {
        private const val ARCHIVO = "comunicaplus_prefs"
        private const val CLAVE_ID_DISPOSITIVO = "id_dispositivo"
        private const val CLAVE_TAMANO_TEXTO = "tamano_texto_hablar"
        const val TAMANO_TEXTO_INICIAL = 28f
    }
}
