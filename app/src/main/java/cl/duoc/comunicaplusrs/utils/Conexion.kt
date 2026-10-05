package cl.duoc.comunicaplusrs.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

// Avisa cada vez que el teléfono gana o pierde conexión a internet.
fun Context.observarConexion(): Flow<Boolean> = callbackFlow {
    val manager = getSystemService<ConnectivityManager>()

    if (manager == null) {
        trySend(true)
        close()
        return@callbackFlow
    }

    val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            trySend(true)
        }

        override fun onLost(network: Network) {
            trySend(false)
        }
    }

    trySend(manager.hayInternet())
    manager.registerDefaultNetworkCallback(callback)

    awaitClose {
        manager.unregisterNetworkCallback(callback)
    }
}.distinctUntilChanged()

private fun ConnectivityManager.hayInternet(): Boolean {
    val capacidades = getNetworkCapabilities(activeNetwork) ?: return false
    return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
