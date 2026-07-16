package cc.rccstudios.map.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cc.rccstudios.map.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val REGISTER_DATA = Pair(
            stringPreferencesKey("register_key"),
            stringPreferencesKey("register_name")
        )
        private val BACKEND_URL = stringPreferencesKey("backend_url")
        private val BATTERY_TRACKER_ENABLED = booleanPreferencesKey("battery_tracker_enabled")
        private val LOCATION_TRACKER_ENABLED = booleanPreferencesKey("location_tracker_enabled")
        private val NETWORK_TRACKER_ENABLED = booleanPreferencesKey("network_tracker_enabled")
        private val SCREEN_LOCK_TRACKER_ENABLED = booleanPreferencesKey("screen_lock_tracker_enabled")
    }

    override suspend fun getToken(): String? {
        return dataStore.data
            .map { preferences -> preferences[TOKEN] }
            .first()
    }

    override suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    override suspend fun saveRegisterData(registerData: Pair<String, String>) {
        dataStore.edit { preferences ->
            preferences[REGISTER_DATA.first] = registerData.first
            preferences[REGISTER_DATA.second] = registerData.second
        }
    }

    override suspend fun getRegisterData(): Pair<String?, String?> {
        return dataStore.data
            .map { preferences -> Pair(
                preferences[REGISTER_DATA.first],
                preferences[REGISTER_DATA.second]
            ) }
            .first()
    }

    override suspend fun saveBackendUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[BACKEND_URL] = url
        }
    }

    override suspend fun getBackendUrl(): String? {
        return dataStore.data
            .map { preferences -> preferences[BACKEND_URL] }
            .first()
    }

    override suspend fun saveBatteryTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BATTERY_TRACKER_ENABLED] = enabled
        }
    }

    override suspend fun getBatteryTrackingEnabled(): Boolean? {
        return dataStore.data
            .map { preferences -> preferences[BATTERY_TRACKER_ENABLED] }
            .first()
    }

    override suspend fun saveLocationTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOCATION_TRACKER_ENABLED] = enabled
        }
    }

    override suspend fun getLocationTrackingEnabled(): Boolean? {
        return dataStore.data
            .map { preferences -> preferences[LOCATION_TRACKER_ENABLED] }
            .first()
    }

    override suspend fun saveNetworkTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NETWORK_TRACKER_ENABLED] = enabled
        }
    }

    override suspend fun getNetworkTrackingEnabled(): Boolean? {
        return dataStore.data
            .map { preferences -> preferences[NETWORK_TRACKER_ENABLED] }
            .first()
    }

    override suspend fun saveScreenLockTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SCREEN_LOCK_TRACKER_ENABLED] = enabled
        }
    }

    override suspend fun getScreenLockTrackingEnabled(): Boolean? {
        return dataStore.data
            .map { preferences -> preferences[SCREEN_LOCK_TRACKER_ENABLED] }
            .first()
    }
}