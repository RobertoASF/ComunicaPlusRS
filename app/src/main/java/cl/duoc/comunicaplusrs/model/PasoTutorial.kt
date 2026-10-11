package cl.duoc.comunicaplusrs.model

// Cada página del tutorial de la pantalla Ayuda.
// "seccion" coincide con la ruta de la pantalla, así el botón de ayuda
// de cada pantalla abre directamente su página.
data class PasoTutorial(
    val seccion: String,
    val titulo: String,
    val descripcion: String,
    val consejos: List<String>
)

const val SECCION_INICIO = "inicio"

val PASOS_TUTORIAL = listOf(
    PasoTutorial(
        seccion = SECCION_INICIO,
        titulo = "Bienvenido a Comunica Plus RS",
        descripcion = "Esta app te ayuda a comunicarte con personas oyentes: lo que escribes se lee en voz alta y lo que te dicen aparece escrito.",
        consejos = listOf(
            "Inicia sesión con tu correo para que tus datos queden guardados en la nube.",
            "En el menú principal eliges qué quieres hacer.",
            "Toca el signo de pregunta de arriba en cualquier pantalla para volver a esta ayuda."
        )
    ),
    PasoTutorial(
        seccion = "escribir",
        titulo = "Escribir",
        descripcion = "Escribe lo que quieres decir y toca LEER para que el teléfono lo diga en voz alta.",
        consejos = listOf(
            "Las frases rápidas te ahorran escribir lo mismo cada vez.",
            "MOSTRAR deja el mensaje en letras grandes para enseñar la pantalla.",
            "Guarda tus frases frecuentes o envíalas por SMS a un contacto."
        )
    ),
    PasoTutorial(
        seccion = "hablar",
        titulo = "Hablar",
        descripcion = "Toca el micrófono y pide a la otra persona que hable cerca del teléfono. Lo que diga aparecerá escrito.",
        consejos = listOf(
            "Ajusta el tamaño de la letra con la barra.",
            "Escribe tu respuesta y la app la leerá en voz alta.",
            "Con vibración o alertas visuales activadas, el teléfono te avisa cuando llega un texto nuevo."
        )
    ),
    PasoTutorial(
        seccion = "historial",
        titulo = "Historial",
        descripcion = "Cada conversación queda guardada: lo que te dijeron y lo que respondiste.",
        consejos = listOf(
            "Filtra para ver solo lo escuchado o solo lo escrito.",
            "Busca una palabra para encontrar una conversación anterior.",
            "Puedes volver a leer un mensaje, eliminarlo o borrar todo el historial."
        )
    ),
    PasoTutorial(
        seccion = "buscar_dispositivo",
        titulo = "Buscar dispositivo",
        descripcion = "Guarda la ubicación de tus teléfonos para encontrarlos si se te pierden.",
        consejos = listOf(
            "Toca Actualizar para guardar dónde está este teléfono.",
            "Ver mapa abre la ubicación en la app de mapas.",
            "Activa la distancia para saber qué tan lejos está cada teléfono."
        )
    ),
    PasoTutorial(
        seccion = "perfil",
        titulo = "Mi perfil y accesibilidad",
        descripcion = "Ajusta la app a tus necesidades. Los cambios se aplican en todas las pantallas.",
        consejos = listOf(
            "Texto grande aumenta el tamaño de todas las letras.",
            "Alto contraste usa fondo negro con letras amarillas.",
            "Vibración y alertas visuales reemplazan los avisos con sonido."
        )
    ),
    PasoTutorial(
        seccion = "widget",
        titulo = "Acceso rápido",
        descripcion = "Agrega el widget a la pantalla de inicio del teléfono para abrir Escribir o Hablar con un solo toque.",
        consejos = listOf(
            "Mantén presionada la pantalla de inicio y elige Widgets.",
            "Busca Comunica Plus RS y arrastra el widget.",
            "Para usarlo debes tener la sesión iniciada en la app."
        )
    )
)

// Si la sección no existe se parte desde la primera página.
fun indiceDeSeccion(seccion: String?): Int {
    return PASOS_TUTORIAL
        .indexOfFirst { it.seccion == seccion }
        .coerceAtLeast(0)
}
