package cc.rccstudios.map.data.repository

import cc.rccstudios.map.data.network.ApiService
import cc.rccstudios.map.data.network.model.toDto
import cc.rccstudios.map.domain.model.Telemetry
import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.domain.repository.TelemetryRepository
import cc.rccstudios.map.domain.tracker.BatteryTracker
import cc.rccstudios.map.domain.tracker.LocationTracker
import cc.rccstudios.map.domain.tracker.NetworkTracker
import cc.rccstudios.map.domain.tracker.ScreenLockTracker
import kotlinx.coroutines.withContext

class TelemetryRepositoryImpl(
    private val apiService: ApiService,
    private val settingsRepository: SettingsRepository,
    private val batteryTracker: BatteryTracker,
    private val locationTracker: LocationTracker,
    private val networkTracker: NetworkTracker,
    private val screenLockTracker: ScreenLockTracker
) : TelemetryRepository {
    override suspend fun sendTelemetry(telemetry: Telemetry): Result<Unit> {
//        if (!(settingsRepository.getTelemetryEnabled() ?: true)) {
//            return Result.success(Unit)
//        }

        return try {
            val response = apiService.sendTelemetry(telemetry.toDto())

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun collectTelemetry(): Telemetry {
        return withContext(kotlinx.coroutines.Dispatchers.IO) {
            val isBatteryTrackingEnabled = settingsRepository.getBatteryTrackingEnabled()
            val isLocationTrackingEnabled = settingsRepository.getLocationTrackingEnabled()
            val isNetworkTrackingEnabled = settingsRepository.getNetworkTrackingEnabled()
            val isScreenLockTrackingEnabled = settingsRepository.getScreenLockTrackingEnabled()
            val batteryStatus = if (isBatteryTrackingEnabled) batteryTracker.getBatteryStatus() else null
            val locationStatus = if (isLocationTrackingEnabled) locationTracker.getLocationStatus() else null
            val networkStatus = if (isNetworkTrackingEnabled) networkTracker.getNetworkStatus() else null
            val screenLockStatus = if (isScreenLockTrackingEnabled) screenLockTracker.getScreenLockStatus() else null

            Telemetry(
                latitude = locationStatus?.first,
                longitude = locationStatus?.second,
                batteryStatus = batteryStatus,
                networkStatus = networkStatus,
                screenLockStatus = screenLockStatus
            )
        }
    }
}