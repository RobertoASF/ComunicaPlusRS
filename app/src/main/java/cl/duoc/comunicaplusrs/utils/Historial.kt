package cl.duoc.comunicaplusrs.utils

import cl.duoc.comunicaplusrs.model.Mensaje
import cl.duoc.comunicaplusrs.model.esEscrito
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

// Funciones de la pantalla Historial. Están separadas de la pantalla
// para poder probarlas con JUnit sin Android.

enum class FiltroHistorial(val etiqueta: String) {
    TODOS("Todos"),
    ESCUCHADOS("Escuchados"),
    ESCRITOS("Escritos")
}

data class ResumenHistorial(
    val total: Int = 0,
    val escuchados: Int = 0,
    val escritos: Int = 0
)

fun List<Mensaje>.filtrar(filtro: FiltroHistorial, busqueda: String): List<Mensaje> {
    val palabra = busqueda.trim()

    return filter { mensaje ->
        when (filtro) {
            FiltroHistorial.TODOS -> true
            FiltroHistorial.ESCUCHADOS -> !mensaje.esEscrito
            FiltroHistorial.ESCRITOS -> mensaje.esEscrito
        }
    }.filter { mensaje ->
        palabra.isEmpty() || mensaje.texto.contains(palabra, ignoreCase = true)
    }
}

// groupBy mantiene el orden, así el día más reciente queda primero.
fun List<Mensaje>.agruparPorDia(
    ahora: Long,
    zona: TimeZone = TimeZone.getDefault()
): Map<String, List<Mensaje>> {
    return sortedByDescending { it.fecha }
        .groupBy { tituloDelDia(it.fecha, ahora, zona) }
}

// partition separa la lista en dos: los que cumplen la condición y los que no.
fun List<Mensaje>.resumen(): ResumenHistorial {
    val (escritos, escuchados) = partition { it.esEscrito }

    return ResumenHistorial(
        total = size,
        escuchados = escuchados.size,
        escritos = escritos.size
    )
}

// "Hoy", "Ayer" o la fecha completa, por ejemplo "Lunes 6 de octubre".
fun tituloDelDia(
    millis: Long,
    ahora: Long,
    zona: TimeZone = TimeZone.getDefault()
): String {
    val dia = SimpleDateFormat("yyyyMMdd", LOCALE_CHILE).apply { timeZone = zona }

    val ayer = Calendar.getInstance(zona).apply {
        timeInMillis = ahora
        add(Calendar.DAY_OF_YEAR, -1)
    }

    return when (dia.format(Date(millis))) {
        dia.format(Date(ahora)) -> "Hoy"
        dia.format(ayer.time) -> "Ayer"
        else -> SimpleDateFormat("EEEE d 'de' MMMM", LOCALE_CHILE)
            .apply { timeZone = zona }
            .format(Date(millis))
            .replaceFirstChar { it.titlecase(LOCALE_CHILE) }
    }
}
