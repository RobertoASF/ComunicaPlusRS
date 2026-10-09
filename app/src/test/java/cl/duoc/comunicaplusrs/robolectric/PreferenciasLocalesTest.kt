package cl.duoc.comunicaplusrs.robolectric

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import cl.duoc.comunicaplusrs.data.PreferenciasLocales
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PreferenciasLocalesTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun idDelDispositivo_seCreaUnaVezYSeMantiene() {
        val primerId = PreferenciasLocales(context).idDispositivo()
        val segundoId = PreferenciasLocales(context).idDispositivo()

        assertTrue(primerId.isNotBlank())
        assertEquals(primerId, segundoId)
    }

    @Test
    fun tamanoDeTexto_seGuardaEnSharedPreferences() {
        PreferenciasLocales(context).tamanoTextoHablar = 40f

        assertEquals(40f, PreferenciasLocales(context).tamanoTextoHablar, 0.01f)
    }

    @Test
    fun tamanoDeTexto_tieneValorInicial() {
        assertEquals(
            PreferenciasLocales.TAMANO_TEXTO_INICIAL,
            PreferenciasLocales(context).tamanoTextoHablar,
            0.01f
        )
    }
}
