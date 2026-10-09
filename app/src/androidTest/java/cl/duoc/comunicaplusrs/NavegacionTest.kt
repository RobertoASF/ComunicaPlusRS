package cl.duoc.comunicaplusrs

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith

// Pruebas instrumentadas: se ejecutan en el emulador o en Firebase Test Lab.
@RunWith(AndroidJUnit4::class)
class NavegacionTest {

    // Antes de abrir la app se cierra la sesión para que siempre parta en el Login.
    @get:Rule(order = 0)
    val sinSesion = object : ExternalResource() {
        override fun before() {
            Firebase.auth.signOut()
        }
    }

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun alAbrirLaApp_seMuestraElLogin() {
        composeRule.onNodeWithText("Comunica Plus RS").assertIsDisplayed()
        composeRule.onNodeWithText("INICIAR SESIÓN").assertIsDisplayed()
    }

    @Test
    fun loginSinDatos_muestraMensajeDeError() {
        composeRule.onNodeWithText("INICIAR SESIÓN").performClick()

        composeRule.onNodeWithText("Debes ingresar tu correo.").assertIsDisplayed()
    }

    @Test
    fun crearCuenta_abreRegistroYElBotonAtrasVuelveAlLogin() {
        composeRule.onNodeWithText("Crear una cuenta").performClick()
        composeRule.onNodeWithText("Crear cuenta").assertIsDisplayed()

        // En Android 8 el primer campo toma el foco y abre el teclado;
        // si no se cierra, el botón atrás solo esconde el teclado.
        Espresso.closeSoftKeyboard()

        // Espresso simula el botón atrás del teléfono.
        Espresso.pressBack()

        composeRule.onNodeWithText("INICIAR SESIÓN").assertIsDisplayed()
    }

    @Test
    fun olvideMiContrasena_abreRecuperarYVuelve() {
        composeRule.onNodeWithText("¿Olvidaste tu contraseña?").performClick()
        composeRule.onNodeWithText("Recuperar contraseña").assertIsDisplayed()

        composeRule.onNodeWithText("Volver al inicio de sesión")
            .performScrollTo()
            .performClick()

        composeRule.onNodeWithText("INICIAR SESIÓN").assertIsDisplayed()
    }

    @Test
    fun registroSinTerminos_muestraError() {
        composeRule.onNodeWithText("Crear una cuenta").performClick()

        composeRule.onNodeWithText("REGISTRAR")
            .performScrollTo()
            .performClick()

        composeRule.onNodeWithText("Debes ingresar tu nombre.")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
