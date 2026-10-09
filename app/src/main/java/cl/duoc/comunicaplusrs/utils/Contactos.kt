package cl.duoc.comunicaplusrs.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.activity.result.contract.ActivityResultContract

data class Contacto(
    val nombre: String,
    val numero: String
)

// Abre la lista de contactos del teléfono para elegir un número.
// Al elegir desde Phone.CONTENT_URI el sistema da permiso de lectura
// solo para ese contacto, así no se necesita el permiso READ_CONTACTS.
class ElegirTelefono : ActivityResultContract<Unit, Uri?>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(Intent.ACTION_PICK, Phone.CONTENT_URI)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }
}

// Consulta el ContentProvider de Contactos con la uri que devolvió el selector.
fun leerContacto(resolver: ContentResolver, uri: Uri): Contacto? {
    val columnas = arrayOf(Phone.DISPLAY_NAME, Phone.NUMBER)

    resolver.query(uri, columnas, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(Phone.DISPLAY_NAME))
            val numero = cursor.getString(cursor.getColumnIndexOrThrow(Phone.NUMBER))

            return Contacto(
                nombre = nombre.orEmpty(),
                numero = numero.orEmpty()
            )
        }
    }
    return null
}
