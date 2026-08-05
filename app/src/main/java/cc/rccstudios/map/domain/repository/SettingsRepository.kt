package cc.rccstudios.map.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val tokenFlow: Flow<String?>
    val usernameFlow: Flow<String?>
    val otpFlow: Flow<String?>
    val serverUrlFlow: Flow<String?>
    val authModeFlow: Flow<Int?>
    val avatarPathFlow: Flow<String?>
    val batteryTrackingEnabledFlow: Flow<Boolean>
    val locationTrackingEnabledFlow: Flow<Boolean>
    val networkTrackingEnabledFlow: Flow<Boolean>
    val screenLockTrackingEnabledFlow: Flow<Boolean>
    val telemetryIntervalFlow: Flow<Long>

    suspend fun saveToken(token: String)
    suspend fun saveUsername(username: String)
    suspend fun saveOtp(otp: String)
    suspend fun saveServerUrl(url: String)
    suspend fun saveAuthMode(authMode: Int)
    suspend fun saveAvatarPath(avatarPath: String)
    suspend fun saveBatteryTrackingEnabled(enabled: Boolean)
    suspend fun saveLocationTrackingEnabled(enabled: Boolean)
    suspend fun saveNetworkTrackingEnabled(enabled: Boolean)
    suspend fun saveScreenLockTrackingEnabled(enabled: Boolean)
    suspend fun saveTelemetryInterval(interval: Long)

    suspend fun getToken(): String?
    suspend fun getUsername(): String?
    suspend fun getOtp(): String?
    suspend fun getServerUrl(): String?
    suspend fun getAuthMode(): Int?
    suspend fun getAvatarPath(): String?
    suspend fun getBatteryTrackingEnabled(): Boolean
    suspend fun getLocationTrackingEnabled(): Boolean
    suspend fun getNetworkTrackingEnabled(): Boolean
    suspend fun getScreenLockTrackingEnabled(): Boolean
    suspend fun getTelemetryInterval(): Long
}