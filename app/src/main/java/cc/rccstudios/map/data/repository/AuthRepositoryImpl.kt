package cc.rccstudios.map.data.repository

import cc.rccstudios.map.data.network.ApiService
import cc.rccstudios.map.data.network.model.toDto
import cc.rccstudios.map.domain.model.Register
import cc.rccstudios.map.domain.repository.AuthRepository
import cc.rccstudios.map.domain.repository.SettingsRepository

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val settingsRepository: SettingsRepository
) : AuthRepository {
    override suspend fun register(register: Register): Result<Unit> {
        return try {
            val response = apiService.register(register.toDto())

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    settingsRepository.saveToken(body.token)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Received null from server"))
                }
            } else {
                Result.failure(Exception("HTTP code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOtp(): Result<Unit> {
        return try {
            val response = apiService.getOtp()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    settingsRepository.saveOtp(body.otp)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Received null from server"))
                }
            } else {
                Result.failure(Exception("HTTP code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getToken(): Result<Unit> {
        return try {
            val response = apiService.getToken()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    settingsRepository.saveToken(body.token)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Received null from server"))
                }
            } else {
                Result.failure(Exception("HTTP code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}