package cl.duoc.comunicaplusrs.navigation

object Routes {

    const val LOGIN = "login"
    const val REGISTER = "register"
    const val RECOVERY = "recovery"
    const val HOME = "home"
    const val ESCRIBIR = "escribir"
    const val HABLAR = "hablar"
    const val BUSCAR_DISPOSITIVO = "buscar_dispositivo"
    const val PERFIL = "perfil"
    const val HISTORIAL = "historial"

    // La ayuda recibe la sección que se quiere ver (argumento opcional).
    const val ARG_SECCION = "seccion"
    const val AYUDA = "ayuda?$ARG_SECCION={$ARG_SECCION}"

    fun ayuda(seccion: String) = "ayuda?$ARG_SECCION=$seccion"
}
