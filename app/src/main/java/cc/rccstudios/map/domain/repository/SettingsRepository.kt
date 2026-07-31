package cc.rccstudios.map.domain.repository

interface SettingsRepository {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?

    suspend fun saveUsername(username: String)
    suspend fun getUsername(): String?

    suspend fun saveOtp(otp: String)
    suspend fun getOtp(): String?

    suspend fun saveServerUrl(url: String)
    suspend fun getServerUrl(): String?

    suspend fun saveBatteryTrackingEnabled(enabled: Boolean)
    suspend fun getBatteryTrackingEnabled(): Boolean?

    suspend fun saveLocationTrackingEnabled(enabled: Boolean)
    suspend fun getLocationTrackingEnabled(): Boolean?

    suspend fun saveNetworkTrackingEnabled(enabled: Boolean)
    suspend fun getNetworkTrackingEnabled(): Boolean?

    suspend fun saveScreenLockTrackingEnabled(enabled: Boolean)
    suspend fun getScreenLockTrackingEnabled(): Boolean?
}