package cl.duoc.comunicaplusrs.model

import com.google.firebase.firestore.DocumentId

// Se usa para las frases guardadas en Escribir y para
// la conversación que se guarda en Hablar.
data class Mensaje(
    @DocumentId
    val id: String = "",
    val texto: String = "",
    val fecha: Long = 0L,
    // Solo en conversaciones: indica quién dijo el mensaje.
    val origen: String = ""
)

// Lo dijo la otra persona y la app lo pasó a texto.
const val ORIGEN_ESCUCHADO = "escuchado"

// Lo escribió el usuario y la app lo leyó en voz alta.
const val ORIGEN_ESCRITO = "escrito"

// Los mensajes antiguos no tienen origen; esos venían de Hablar, así que cuentan como escuchados.
val Mensaje.esEscrito: Boolean
    get() = origen == ORIGEN_ESCRITO
