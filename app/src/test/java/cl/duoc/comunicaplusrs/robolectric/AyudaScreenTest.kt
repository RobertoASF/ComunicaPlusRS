package cl.duoc.comunicaplusrs.robolectric

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import cl.duoc.comunicaplusrs.model.PASOS_TUTORIAL
import cl.duoc.comunicaplusrs.navigation.Routes
import cl.duoc.comunicaplusrs.ui.screens.AyudaScreen
import cl.duoc.comunicaplusrs.ui.theme.ComunicaPlusRSTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AyudaScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun abrirAyuda(seccion: String?, onVolver: () -> Unit = {}) {
        composeRule.setContent {
            ComunicaPlusRSTheme {
                AyudaScreen(
                    seccionInicial = seccion,
                    onVolver = onVolver
                )
            }
        }
    }

    @Test
    fun sinSeccion_partePorLaBienvenida() {
        abrirAyuda(null)

        composeRule.onNodeWithText("Bienvenido a Comunica Plus RS").assertIsDisplayed()
        composeRule.onNodeWithText("Paso 1 de ${PASOS_TUTORIAL.size}").assertIsDisplayed()
        composeRule.onNodeWithText("ANTERIOR").assertIsNotEnabled()
    }

    @Test
    fun siguiente_pasaALaPaginaDeEscribir() {
        abrirAyuda(null)

        composeRule.onNodeWithText("SIGUIENTE").performClick()

        composeRule.onNodeWithText("Paso 2 de ${PASOS_TUTORIAL.size}").assertIsDisplayed()
        composeRule.onNodeWithText("Escribir").assertIsDisplayed()
    }

    @Test
    fun desdeHablar_abreDirectoSuPagina() {
        abrirAyuda(Routes.HABLAR)

        composeRule.onNodeWithText("Paso 3 de ${PASOS_TUTORIAL.size}").assertIsDisplayed()
        // El consejo está más abajo en la página, por eso primero se hace scroll.
        composeRule.onNodeWithText("Escribe tu respuesta y la app la leerá en voz alta.")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun enLaUltimaPagina_entendidoVuelveALaPantallaAnterior() {
        var volvio = false
        abrirAyuda("widget", onVolver = { volvio = true })

        composeRule.onNodeWithText("ENTENDIDO").performClick()

        assertTrue(volvio)
    }
}
