package cl.duoc.comunicaplusrs.navigation

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.comunicaplusrs.model.PreferenciasAccesibilidad
import cl.duoc.comunicaplusrs.model.Usuario
import cl.duoc.comunicaplusrs.model.preferencias
import cl.duoc.comunicaplusrs.ui.components.AvisoSinConexion
import cl.duoc.comunicaplusrs.ui.screens.BuscarDispositivoScreen
import cl.duoc.comunicaplusrs.ui.screens.EscribirScreen
import cl.duoc.comunicaplusrs.ui.screens.HablarScreen
import cl.duoc.comunicaplusrs.ui.screens.HomeScreen
import cl.duoc.comunicaplusrs.ui.screens.LoginScreen
import cl.duoc.comunicaplusrs.ui.screens.PerfilScreen
import cl.duoc.comunicaplusrs.ui.screens.RecoveryScreen
import cl.duoc.comunicaplusrs.ui.screens.RegisterScreen
import cl.duoc.comunicaplusrs.ui.theme.ComunicaPlusRSTheme
import cl.duoc.comunicaplusrs.viewmodel.PerfilViewModel

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

    NavHost(
        navController = navController,
        startDestination = inicio
    ) {

        composable(Routes.LOGIN) {

            LoginScreen(
                onGoToRegister = {
                    navController.navigate(Routes.REGISTER)
                },

                onGoToRecovery = {
                    navController.navigate(Routes.RECOVERY)
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
                }
            )
        }

        composable(Routes.HABLAR) {

            HablarScreen(
                preferencias = preferencias,
                onVolver = {
                    navController.popBackStack()
                },
                onResponder = {
                    navController.navigate(Routes.ESCRIBIR)
                }
            )
        }

        composable(Routes.BUSCAR_DISPOSITIVO) {

            BuscarDispositivoScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PERFIL) {

            PerfilScreen(
                viewModel = perfilViewModel,
                onVolver = {
                    navController.popBackStack()
                },
                onCuentaEliminada = irAlLogin
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
