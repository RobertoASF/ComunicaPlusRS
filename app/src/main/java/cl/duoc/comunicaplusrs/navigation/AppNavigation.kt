package cl.duoc.comunicaplusrs.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.comunicaplusrs.ui.screens.HomeScreen
import cl.duoc.comunicaplusrs.ui.screens.LoginScreen
import cl.duoc.comunicaplusrs.ui.screens.RecoveryScreen
import cl.duoc.comunicaplusrs.ui.screens.RegisterScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {

            LoginScreen(
                onGoToRegister = {
                    navController.navigate(Routes.REGISTER)
                },

                onGoToRecovery = {
                    navController.navigate(Routes.RECOVERY)
                },

                onLoginSuccess = {
                    // Se quita el Login de la pila para que el botón atrás no vuelva a él.
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.HOME) {

            HomeScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {

            RegisterScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.RECOVERY) {

            RecoveryScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}