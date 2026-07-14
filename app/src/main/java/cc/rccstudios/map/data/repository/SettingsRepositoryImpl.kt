package cc.rccstudios.map.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cc.rccstudios.map.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    companion object {
        private val BACKEND_URL = stringPreferencesKey("backend_url")
    }

    override suspend fun getBackendUrl(): String? {
        return dataStore.data
            .map { preferences -> preferences[BACKEND_URL] }
            .first()
    }

    override suspend fun saveBackendUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[BACKEND_URL] = url
        }
    }
}