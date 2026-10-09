package cl.duoc.comunicaplusrs.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.getSystemService

// Aviso por vibración cuando llega texto nuevo en Hablar,
// para usuarios que activaron la opción "Vibración" en su perfil.
fun Context.vibrarAviso() {
    val vibrador = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService<VibratorManager>()?.defaultVibrator
    } else {
        getSystemService<Vibrator>()
    } ?: return

    if (!vibrador.hasVibrator()) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrador.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrador.vibrate(400)
    }
}
