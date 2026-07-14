package cc.rccstudios.map.domain.repository

interface SettingsRepository {
    suspend fun getBackendUrl(): String?
    suspend fun saveBackendUrl(url: String)
}