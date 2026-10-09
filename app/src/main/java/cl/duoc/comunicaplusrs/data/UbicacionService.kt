package cl.duoc.comunicaplusrs.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

data class Ubicacion(
    val latitud: Double,
    val longitud: Double,
    val precision: Float
)

interface ProveedorUbicacion {

    suspend fun obtenerUbicacion(): Ubicacion?

    suspend fun obtenerDireccion(latitud: Double, longitud: Double): String
}

// Usa el GPS / red del teléfono a través de Google Play Services.
// Los permisos de ubicación se piden en el Fragment antes de llamar a esta clase.
class GpsUbicacionService(context: Context) : ProveedorUbicacion {

    private val cliente = LocationServices.getFusedLocationProviderClient(context)

    private val geocoder = Geocoder(context, Locale("es", "CL"))

    @SuppressLint("MissingPermission")
    override suspend fun obtenerUbicacion(): Ubicacion? {
        val token = CancellationTokenSource()

        // Si no se puede obtener una ubicación nueva se usa la última conocida.
        val location = cliente
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
            .await()
            ?: cliente.lastLocation.await()

        return location?.let {
            Ubicacion(it.latitude, it.longitude, it.accuracy)
        }
    }

    override suspend fun obtenerDireccion(latitud: Double, longitud: Double): String {
        if (!Geocoder.isPresent()) return ""

        return withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                geocoder
                    .getFromLocation(latitud, longitud, 1)
                    ?.firstOrNull()
                    ?.getAddressLine(0)
                    .orEmpty()
            } catch (e: Exception) {
                // Sin internet el Geocoder falla; la dirección es opcional.
                ""
            }
        }
    }
}
