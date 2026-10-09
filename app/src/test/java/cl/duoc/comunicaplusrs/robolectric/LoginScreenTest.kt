package cl.duoc.comunicaplusrs.robolectric

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import cl.duoc.comunicaplusrs.data.AuthService
import cl.duoc.comunicaplusrs.ui.screens.LoginScreen
import cl.duoc.comunicaplusrs.ui.theme.ComunicaPlusRSTheme
import cl.duoc.comunicaplusrs.viewmodel.LoginViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Prueba de la pantalla Login sin emulador: Robolectric simula Android en la JVM.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val authService: AuthService = mock()

    private fun abrirLogin(onLoginSuccess: () -> Unit = {}) {
        composeRule.setContent {
            ComunicaPlusRSTheme {
                LoginScreen(
                    onGoToRegister = {},
                    onGoToRecovery = {},
                    onLoginSuccess = onLoginSuccess,
                    viewModel = LoginViewModel(authService)
                )
            }
        }
    }

    @Test
    fun muestraLosComponentesPrincipales() {
        abrirLogin()

        composeRule.onNodeWithText("Comunica Plus RS").assertIsDisplayed()
        composeRule.onNodeWithText("INICIAR SESIÓN").assertIsDisplayed()
        composeRule.onNodeWithText("Crear una cuenta").assertIsDisplayed()
    }

    @Test
    fun ingresarSinDatos_muestraElErrorEnPantalla() {
        abrirLogin()

        composeRule.onNodeWithText("INICIAR SESIÓN").performClick()

        composeRule.onNodeWithText("Debes ingresar tu correo.").assertIsDisplayed()
    }

    @Test
    fun ingresarConDatos_llamaAlServicioYPasaAlMenu() {
        var pasoAlMenu = false
        abrirLogin(onLoginSuccess = { pasoAlMenu = true })

        composeRule.onNodeWithText("Correo electrónico").performTextInput("ana@duoc.cl")
        composeRule.onNodeWithText("Contraseña").performTextInput("123456")
        composeRule.onNodeWithText("INICIAR SESIÓN").performClick()
        composeRule.waitForIdle()

        runBlocking {
            verify(authService).iniciarSesion("ana@duoc.cl", "123456")
        }
        assertTrue(pasoAlMenu)
    }
}
