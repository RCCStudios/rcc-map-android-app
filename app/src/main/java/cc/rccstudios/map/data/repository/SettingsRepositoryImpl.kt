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
        private val TOKEN = stringPreferencesKey("token")
        private val REGISTER_DATA = Pair(
            stringPreferencesKey("register_key"),
            stringPreferencesKey("register_name")
        )
        private val BACKEND_URL = stringPreferencesKey("backend_url")
    }

    override suspend fun getToken(): String? {
        return dataStore.data
            .map { preferences -> preferences[TOKEN] }
            .first()
    }

    override suspend fun saveToken(token: String) {
        if (getToken() == null) {
            dataStore.edit { preferences ->
                preferences[TOKEN] = token
            }
        }
    }

//    override suspend fun saveRegisterData(registerData: Pair<String, String>) {
//        dataStore.edit { preferences ->
//            preferences[REGISTER_DATA.first] = registerData.first
//            preferences[REGISTER_DATA.second] = registerData.second
//        }
//    }
//
//    override suspend fun getRegisterData(): Pair<String?, String?> {
//        return dataStore.data
//            .map { preferences -> Pair(
//                preferences[REGISTER_DATA.first],
//                preferences[REGISTER_DATA.second]
//            ) }
//            .first()
//    }

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