package cl.duoc.comunicaplusrs.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import cl.duoc.comunicaplusrs.MainActivity
import cl.duoc.comunicaplusrs.R
import cl.duoc.comunicaplusrs.navigation.Routes

// Widget de la pantalla de inicio con dos botones: Escribir y Hablar.
// Sirve para abrir rápido la función que se necesita en una conversación.
class AccesoRapidoWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            val vistas = RemoteViews(context.packageName, R.layout.widget_acceso_rapido)

            vistas.setOnClickPendingIntent(
                R.id.btnWidgetEscribir,
                crearPendingIntent(context, Routes.ESCRIBIR, CODIGO_ESCRIBIR)
            )
            vistas.setOnClickPendingIntent(
                R.id.btnWidgetHablar,
                crearPendingIntent(context, Routes.HABLAR, CODIGO_HABLAR)
            )

            appWidgetManager.updateAppWidget(id, vistas)
        }
    }

    companion object {
        private const val CODIGO_ESCRIBIR = 1
        private const val CODIGO_HABLAR = 2

        fun crearIntentDestino(context: Context, destino: String): Intent {
            return Intent(context, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_DESTINO, destino)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        // Cada botón usa un código distinto para que Android no mezcle los PendingIntent.
        private fun crearPendingIntent(context: Context, destino: String, codigo: Int): PendingIntent {
            return PendingIntent.getActivity(
                context,
                codigo,
                crearIntentDestino(context, destino),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
