package cl.duoc.comunicaplusrs

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.duoc.comunicaplusrs.ui.fragments.BuscarDispositivoFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// Prueba con Espresso del Fragment hecho con vistas XML.
@RunWith(AndroidJUnit4::class)
class BuscarDispositivoFragmentTest {

    @Before
    fun cerrarSesion() {
        Firebase.auth.signOut()
    }

    private fun abrirFragment() {
        launchFragmentInContainer<BuscarDispositivoFragment>(
            themeResId = R.style.Theme_ComunicaPlusRS
        )
    }

    @Test
    fun muestraLosBotonesDeLaPantalla() {
        abrirFragment()

        onView(withId(R.id.btnActualizarUbicacion)).check(matches(isDisplayed()))
        onView(withId(R.id.btnVerMapa)).check(matches(isDisplayed()))
        onView(withId(R.id.swMostrarDistancia)).check(matches(isDisplayed()))
    }

    @Test
    fun sinUbicacionGuardada_elBotonDelMapaEstaDesactivado() {
        abrirFragment()

        onView(withId(R.id.btnVerMapa)).check(matches(not(isEnabled())))
        onView(withId(R.id.tvCoordenadas))
            .check(matches(withText(R.string.sin_ubicacion)))
    }

    @Test
    fun sinSesion_muestraMensajeParaIniciarSesion() {
        abrirFragment()

        onView(withId(R.id.tvMensaje))
            .check(matches(isDisplayed()))
            .check(matches(withText("Debes iniciar sesión para buscar tus dispositivos.")))
    }

    @Test
    fun elSwitchDeDistancia_cambiaAlTocarlo() {
        abrirFragment()

        onView(withId(R.id.swMostrarDistancia))
            .check(matches(isChecked()))
            .perform(click())
            .check(matches(isNotChecked()))
    }
}
