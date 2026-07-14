package cc.rccstudios.map.domain.repository

interface SettingsRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun getRegisterData(): Pair<String?, String?>
    suspend fun saveRegisterData(registerData: Pair<String, String>)
    suspend fun getBackendUrl(): String?
    suspend fun saveBackendUrl(url: String)
}