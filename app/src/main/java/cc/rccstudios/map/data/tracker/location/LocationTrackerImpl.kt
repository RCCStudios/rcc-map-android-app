package cc.rccstudios.map.data.tracker.location

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await

class LocationTrackerImpl(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) : cc.rccstudios.map.domain.tracker.LocationTracker {
    override suspend fun getLocationStatus(): Pair<Double, Double>? {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return null
        return try {
            val location = fusedLocationClient.Location.await()
            if (location != null) {
                Pair(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}