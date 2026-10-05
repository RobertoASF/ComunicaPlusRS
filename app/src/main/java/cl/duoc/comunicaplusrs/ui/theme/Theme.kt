package cl.duoc.comunicaplusrs.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val AltoContrasteColorScheme = darkColorScheme(
    primary = ContrasteAmarillo,
    onPrimary = ContrasteNegro,
    primaryContainer = ContrasteAmarillo,
    onPrimaryContainer = ContrasteNegro,
    secondary = ContrasteBlanco,
    onSecondary = ContrasteNegro,
    secondaryContainer = ContrasteGris,
    onSecondaryContainer = ContrasteAmarillo,
    background = ContrasteNegro,
    onBackground = ContrasteBlanco,
    surface = ContrasteNegro,
    onSurface = ContrasteBlanco,
    surfaceVariant = ContrasteGris,
    onSurfaceVariant = ContrasteBlanco,
    outline = ContrasteAmarillo,
    error = ContrasteRojo,
    onError = ContrasteNegro,
    errorContainer = ContrasteRojo,
    onErrorContainer = ContrasteNegro
)

// Con "Texto grande" se aumenta un 30% el tamaño de todas las letras de la app.
private const val ESCALA_TEXTO_GRANDE = 1.3f

@Composable
fun ComunicaPlusRSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    altoContraste: Boolean = false,
    textoGrande: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        altoContraste -> AltoContrasteColorScheme

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val densidad = LocalDensity.current
    val escala = if (textoGrande) ESCALA_TEXTO_GRANDE else 1f

    CompositionLocalProvider(
        LocalDensity provides Density(densidad.density, densidad.fontScale * escala)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
