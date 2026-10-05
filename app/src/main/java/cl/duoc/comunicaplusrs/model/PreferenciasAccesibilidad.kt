package cl.duoc.comunicaplusrs.model

data class PreferenciasAccesibilidad(
    val textoGrande: Boolean = false,
    val altoContraste: Boolean = false,
    val alertasVisuales: Boolean = false,
    val vibracion: Boolean = false
)

// Convierte lo que el usuario eligió en el registro en opciones que usa la app.
fun Usuario.preferencias(): PreferenciasAccesibilidad {
    return PreferenciasAccesibilidad(
        textoGrande = preferenciaInterfaz == "Texto grande" ||
                "Texto grande" in opcionesAccesibilidad,
        altoContraste = "Alto contraste" in opcionesAccesibilidad,
        alertasVisuales = "Alertas visuales" in opcionesAccesibilidad,
        vibracion = "Vibración" in opcionesAccesibilidad
    )
}
