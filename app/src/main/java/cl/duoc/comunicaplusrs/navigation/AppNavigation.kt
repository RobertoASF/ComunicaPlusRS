package cl.duoc.comunicaplusrs.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.comunicaplusrs.model.PreferenciasAccesibilidad
import cl.duoc.comunicaplusrs.model.SECCION_INICIO
import cl.duoc.comunicaplusrs.model.Usuario
import cl.duoc.comunicaplusrs.model.preferencias
import cl.duoc.comunicaplusrs.ui.components.AvisoSinConexion
import cl.duoc.comunicaplusrs.ui.screens.AyudaScreen
import cl.duoc.comunicaplusrs.ui.screens.BuscarDispositivoScreen
import cl.duoc.comunicaplusrs.ui.screens.EscribirScreen
import cl.duoc.comunicaplusrs.ui.screens.HablarScreen
import cl.duoc.comunicaplusrs.ui.screens.HistorialScreen
import cl.duoc.comunicaplusrs.ui.screens.HomeScreen
import cl.duoc.comunicaplusrs.ui.screens.LoginScreen
import cl.duoc.comunicaplusrs.ui.screens.PerfilScreen
import cl.duoc.comunicaplusrs.ui.screens.RecoveryScreen
import cl.duoc.comunicaplusrs.ui.screens.RegisterScreen
import cl.duoc.comunicaplusrs.ui.theme.ComunicaPlusRSTheme
import cl.duoc.comunicaplusrs.viewmodel.PerfilViewModel

private const val DURACION_TRANSICION = 300

// Raíz de la app: aplica el tema según las preferencias del usuario
// y muestra el aviso de conexión sobre todas las pantallas.
@Composable
fun ComunicaPlusApp(
    destinoWidget: String?,
    onDestinoAbierto: () -> Unit
) {
    val perfilViewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.Factory)
    val usuario by perfilViewModel.usuario.collectAsStateWithLifecycle()
    val preferencias = usuario?.preferencias() ?: PreferenciasAccesibilidad()

    ComunicaPlusRSTheme(
        altoContraste = preferencias.altoContraste,
        textoGrande = preferencias.textoGrande
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            // safeDrawingPadding evita que el contenido quede debajo de la barra de estado o del teclado.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            ) {
                AvisoSinConexion()

                AppNavigation(
                    perfilViewModel = perfilViewModel,
                    usuario = usuario,
                    preferencias = preferencias,
                    destinoWidget = destinoWidget,
                    onDestinoAbierto = onDestinoAbierto
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    perfilViewModel: PerfilViewModel,
    usuario: Usuario?,
    preferencias: PreferenciasAccesibilidad,
    destinoWidget: String?,
    onDestinoAbierto: () -> Unit
) {

    val navController = rememberNavController()

    // Si ya había una sesión iniciada en Firebase se entra directo al menú.
    val inicio = remember {
        if (perfilViewModel.haySesion()) Routes.HOME else Routes.LOGIN
    }

    val irAlMenu = {
        perfilViewModel.actualizarSesion()
        navController.navigate(Routes.HOME) {
            popUpTo(Routes.LOGIN) {
                inclusive = true
            }
        }
    }

    val irAlLogin = {
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    // Abre la ayuda en la página de la pantalla desde donde se pidió.
    val abrirAyuda: (String) -> Unit = { seccion ->
        navController.navigate(Routes.ayuda(seccion)) {
            launchSingleTop = true
        }
    }

    // Al avanzar la pantalla nueva entra desde la derecha y al volver sale hacia la derecha.
    NavHost(
        navController = navController,
        startDestination = inicio,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(DURACION_TRANSICION)) +
                    fadeIn(tween(DURACION_TRANSICION))
        },
        exitTransition = {
            fadeOut(tween(DURACION_TRANSICION))
        },
        popEnterTransition = {
            fadeIn(tween(DURACION_TRANSICION))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(DURACION_TRANSICION)) +
                    fadeOut(tween(DURACION_TRANSICION))
        }
    ) {

        composable(Routes.LOGIN) {

            LoginScreen(
                onGoToRegister = {
                    navController.navigate(Routes.REGISTER)
                },

                onGoToRecovery = {
                    navController.navigate(Routes.RECOVERY)
                },

                onVerAyuda = {
                    abrirAyuda(SECCION_INICIO)
                },

                // Se quita el Login de la pila para que el botón atrás no vuelva a él.
                onLoginSuccess = irAlMenu
            )
        }

        composable(Routes.REGISTER) {

            RegisterScreen(
                onBackToLogin = {
                    navController.popBackStack()
                },
                onRegistroExitoso = irAlMenu
            )
        }

        composable(Routes.RECOVERY) {

            RecoveryScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HOME) {

            HomeScreen(
                usuario = usuario,
                onAbrir = { ruta ->
                    navController.navigate(ruta)
                },
                onLogout = {
                    perfilViewModel.cerrarSesion()
                    irAlLogin()
                }
            )
        }

        composable(Routes.ESCRIBIR) {

            EscribirScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onAyuda = {
                    abrirAyuda(Routes.ESCRIBIR)
                }
            )
        }

        composable(Routes.HABLAR) {

            HablarScreen(
                preferencias = preferencias,
                onVolver = {
                    navController.popBackStack()
                },
                onVerHistorial = {
                    navController.navigate(Routes.HISTORIAL)
                },
                onAyuda = {
                    abrirAyuda(Routes.HABLAR)
                }
            )
        }

        composable(Routes.HISTORIAL) {

            HistorialScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onAyuda = {
                    abrirAyuda(Routes.HISTORIAL)
                }
            )
        }

        composable(Routes.BUSCAR_DISPOSITIVO) {

            BuscarDispositivoScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onAyuda = {
                    abrirAyuda(Routes.BUSCAR_DISPOSITIVO)
                }
            )
        }

        composable(Routes.PERFIL) {

            PerfilScreen(
                viewModel = perfilViewModel,
                onVolver = {
                    navController.popBackStack()
                },
                onAyuda = {
                    abrirAyuda(Routes.PERFIL)
                },
                onCuentaEliminada = irAlLogin
            )
        }

        // navArgument define el parámetro opcional "seccion" de la ruta de ayuda.
        composable(
            route = Routes.AYUDA,
            arguments = listOf(
                navArgument(Routes.ARG_SECCION) {
                    type = NavType.StringType
                    defaultValue = SECCION_INICIO
                }
            )
        ) { entrada ->

            AyudaScreen(
                seccionInicial = entrada.arguments?.getString(Routes.ARG_SECCION),
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }

    // El widget solo abre Escribir o Hablar si hay una sesión iniciada.
    LaunchedEffect(destinoWidget) {
        if (destinoWidget == null) return@LaunchedEffect

        if (perfilViewModel.haySesion() && destinoWidget in listOf(Routes.ESCRIBIR, Routes.HABLAR)) {
            navController.navigate(destinoWidget) {
                launchSingleTop = true
            }
        }
        onDestinoAbierto()
    }
}
