package cl.duoc.comunicaplusrs.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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