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
        return try {
            val baseUrl = settingsRepository.getBackendUrl() ?: return Result.failure(Exception("No baseUrl"))

            val fullUrl = if (baseUrl.endsWith("/")) "${baseUrl}sendData" else "$baseUrl/sendData"

            val response = apiService.sendTelemetry(fullUrl, telemetry.toDto())

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("CODE: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun collectTelemetry(): Telemetry {
        return withContext(kotlinx.coroutines.Dispatchers.IO) {
            val token = settingsRepository.getToken() ?: ""
            val isBatteryTrackingEnabled = settingsRepository.getBatteryTrackingEnabled() ?: true
            val isLocationTrackingEnabled = settingsRepository.getLocationTrackingEnabled() ?: true
            val isNetworkTrackingEnabled = settingsRepository.getNetworkTrackingEnabled() ?: true
            val isScreenLockTrackingEnabled = settingsRepository.getScreenLockTrackingEnabled() ?: true
            val batteryStatus = if (isBatteryTrackingEnabled) batteryTracker.getBatteryStatus() else null
            val locationStatus = if (isLocationTrackingEnabled) locationTracker.getLocationStatus() else null
            val networkStatus = if (isNetworkTrackingEnabled) networkTracker.getNetworkStatus() else null
            val screenLockStatus = if (isScreenLockTrackingEnabled) screenLockTracker.getScreenLockStatus() else false

            Telemetry(
                token = token,
                latitude = locationStatus?.first,
                longitude = locationStatus?.second,
                batteryPercentage = batteryStatus,
                networkStatus = networkStatus,
                screenLockStatus = screenLockStatus
            )
        }
    }
}