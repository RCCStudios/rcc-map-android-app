package cc.rccstudios.map.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.ui.AuthMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val USERNAME = stringPreferencesKey("username")
        private val OTP = stringPreferencesKey("otp")
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val AUTH_MODE = intPreferencesKey("auth_mode")
        private val AVATAR_PATH = stringPreferencesKey("avatar_path")
        private val BATTERY_TRACKER_ENABLED = booleanPreferencesKey("battery_tracker_enabled")
        private val LOCATION_TRACKER_ENABLED = booleanPreferencesKey("location_tracker_enabled")
        private val NETWORK_TRACKER_ENABLED = booleanPreferencesKey("network_tracker_enabled")
        private val SCREEN_LOCK_TRACKER_ENABLED = booleanPreferencesKey("screen_lock_tracker_enabled")
    }

    override suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }
    override suspend fun getToken(): String? {
        return dataStore.data
            .map { preferences -> preferences[TOKEN] }
            .first()
    }

    override suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }
    override suspend fun getUsername(): String? {
        return dataStore.data
            .map { preferences -> preferences[USERNAME] }
            .first()
    }

    override suspend fun saveOtp(otp: String) {
        dataStore.edit { preferences ->
            preferences[OTP] = otp
        }
    }
    override suspend fun getOtp(): String? {
        return dataStore.data
            .map { preferences -> preferences[OTP] }
            .first()
    }

    override suspend fun saveServerUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_URL] = url
        }
    }
    override suspend fun getServerUrl(): String? {
        return dataStore.data
            .map { preferences -> preferences[SERVER_URL] }
            .first()
    }

    override suspend fun saveAuthMode(authMode: Int) {
        dataStore.edit { preferences ->
            preferences[AUTH_MODE] = authMode
        }
    }
    override suspend fun getAuthMode(): Int? {
        return dataStore.data
            .map { preferences -> preferences[AUTH_MODE] }
            .first()
    }

    override suspend fun saveAvatarPath(avatarPath: String) {
        dataStore.edit { preferences ->
            preferences[AVATAR_PATH] = avatarPath
        }
    }
    override suspend fun getAvatarPath(): String? {
        return dataStore.data
            .map { preferences -> preferences[AVATAR_PATH] }
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