package cc.rccstudios.map.domain.repository

interface SettingsRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun getRegisterData(): Pair<String?, String?>
    suspend fun saveRegisterData(registerData: Pair<String, String>)
    suspend fun getBackendUrl(): String?
    suspend fun saveBackendUrl(url: String)
    suspend fun saveBatteryTrackingEnabled(enabled: Boolean)
    suspend fun getBatteryTrackingEnabled(): Boolean?
    suspend fun saveLocationTrackingEnabled(enabled: Boolean)
    suspend fun getLocationTrackingEnabled(): Boolean?
    suspend fun saveNetworkTrackingEnabled(enabled: Boolean)
    suspend fun getNetworkTrackingEnabled(): Boolean?
    suspend fun saveScreenLockTrackingEnabled(enabled: Boolean)
    suspend fun getScreenLockTrackingEnabled(): Boolean?
}