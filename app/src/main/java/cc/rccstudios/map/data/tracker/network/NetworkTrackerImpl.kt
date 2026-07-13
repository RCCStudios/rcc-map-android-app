package cc.rccstudios.map.data.tracker.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkTrackerImpl(
    private val context: Context
) : cc.rccstudios.map.domain.tracker.NetworkTracker {
    override fun getNetworkStatus(): Int? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return null
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return null
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 1
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> 2
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 3
            else -> 0
        }
    }
}