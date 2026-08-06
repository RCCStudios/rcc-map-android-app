package cc.rccstudios.map.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import cc.rccstudios.map.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    private object PreferencesKeys {
        val TOKEN = stringPreferencesKey("token")
        val USERNAME = stringPreferencesKey("username")
        val OTP = stringPreferencesKey("otp")
        val SERVER_URL = stringPreferencesKey("server_url")
        val AUTH_MODE = intPreferencesKey("auth_mode")
        val AVATAR_PATH = stringPreferencesKey("avatar_path")
        val TELEGRAM = stringPreferencesKey("telegram")
        val BATTERY_TRACKER_ENABLED = booleanPreferencesKey("battery_tracker_enabled")
        val LOCATION_TRACKER_ENABLED = booleanPreferencesKey("location_tracker_enabled")
        val NETWORK_TRACKER_ENABLED = booleanPreferencesKey("network_tracker_enabled")
        val SCREEN_LOCK_TRACKER_ENABLED = booleanPreferencesKey("screen_lock_tracker_enabled")
        val TELEMETRY_INTERVAL = longPreferencesKey("telemetry_interval")
    }

    override val tokenFlow: Flow<String?> = dataStore.data.map { it[PreferencesKeys.TOKEN] }
    override val usernameFlow: Flow<String?> = dataStore.data.map { it[PreferencesKeys.USERNAME] }
    override val otpFlow: Flow<String?> = dataStore.data.map { it[PreferencesKeys.OTP] }
    override val serverUrlFlow: Flow<String?> = dataStore.data.map { it[PreferencesKeys.SERVER_URL] }
    override val authModeFlow: Flow<Int?> = dataStore.data.map { it[PreferencesKeys.AUTH_MODE] }
    override val avatarPathFlow: Flow<String?> = dataStore.data.map { it[PreferencesKeys.AVATAR_PATH] }
    override val telegramFlow: Flow<String?> = dataStore.data.map { it[PreferencesKeys.TELEGRAM] }

    override val batteryTrackingEnabledFlow: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.BATTERY_TRACKER_ENABLED] ?: true }
    override val locationTrackingEnabledFlow: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.LOCATION_TRACKER_ENABLED] ?: true }
    override val networkTrackingEnabledFlow: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.NETWORK_TRACKER_ENABLED] ?: true }
    override val screenLockTrackingEnabledFlow: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.SCREEN_LOCK_TRACKER_ENABLED] ?: true }

    override val telemetryIntervalFlow: Flow<Long> = dataStore.data.map { it[PreferencesKeys.TELEMETRY_INTERVAL] ?: 60000L }

    override suspend fun saveToken(token: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.TOKEN] = token }
    }

    override suspend fun saveUsername(username: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.USERNAME] = username }
    }

    override suspend fun saveOtp(otp: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.OTP] = otp }
    }

    override suspend fun saveServerUrl(url: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.SERVER_URL] = url }
    }

    override suspend fun saveAuthMode(authMode: Int) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.AUTH_MODE] = authMode }
    }

    override suspend fun saveAvatarPath(avatarPath: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.AVATAR_PATH] = avatarPath }
    }

    override suspend fun saveTelegram(telegram: String) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.TELEGRAM] = telegram }
    }

    override suspend fun saveBatteryTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.BATTERY_TRACKER_ENABLED] = enabled }
    }

    override suspend fun saveLocationTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.LOCATION_TRACKER_ENABLED] = enabled }
    }

    override suspend fun saveNetworkTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.NETWORK_TRACKER_ENABLED] = enabled }
    }

    override suspend fun saveScreenLockTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.SCREEN_LOCK_TRACKER_ENABLED] = enabled }
    }

    override suspend fun saveTelemetryInterval(interval: Long) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.TELEMETRY_INTERVAL] = interval }
    }

    override suspend fun getToken(): String? = tokenFlow.first()
    override suspend fun getUsername(): String? = usernameFlow.first()
    override suspend fun getOtp(): String? = otpFlow.first()
    override suspend fun getServerUrl(): String? = serverUrlFlow.first()
    override suspend fun getAuthMode(): Int? = authModeFlow.first()
    override suspend fun getAvatarPath(): String? = avatarPathFlow.first()
    override suspend fun getTelegram(): String? = telegramFlow.first()
    override suspend fun getBatteryTrackingEnabled(): Boolean = batteryTrackingEnabledFlow.first()
    override suspend fun getLocationTrackingEnabled(): Boolean = locationTrackingEnabledFlow.first()
    override suspend fun getNetworkTrackingEnabled(): Boolean = networkTrackingEnabledFlow.first()
    override suspend fun getScreenLockTrackingEnabled(): Boolean = screenLockTrackingEnabledFlow.first()
    override suspend fun getTelemetryInterval(): Long = telemetryIntervalFlow.first()
}