package cc.rccstudios.map.data.repository

import cc.rccstudios.map.data.network.ApiService
import cc.rccstudios.map.data.network.model.toDto
import cc.rccstudios.map.domain.model.Register
import cc.rccstudios.map.domain.repository.RegisterRepository
import cc.rccstudios.map.domain.repository.SettingsRepository
import kotlinx.coroutines.withContext

class RegisterRepositoryImpl(
    private val apiService: ApiService,
    private val settingsRepository: SettingsRepository
) : RegisterRepository {
//    override suspend fun getRegisterData(): Register? {
//        return withContext(kotlinx.coroutines.Dispatchers.IO) {
//            val registerData = settingsRepository.getRegisterData()
//            val key = registerData.first
//            val name = registerData.second
//
//            if (key != null && name != null) {
//                Register(key, name)
//            } else {
//                null
//            }
//        }
//    }

    override suspend fun register(register: Register): Result<Unit> {
        return try {
            val baseUrl = settingsRepository.getBackendUrl() ?: return Result.failure(Exception("No baseUrl"))

            val fullUrl = if (baseUrl.endsWith("/")) "${baseUrl}register" else "$baseUrl/register"

            val response = apiService.register(fullUrl, register.toDto())

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    settingsRepository.saveToken(body.token)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Got null from server"))
                }
            } else {
                Result.failure(Exception("CODE: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}