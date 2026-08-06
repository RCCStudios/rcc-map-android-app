package cc.rccstudios.map.data.repository

import cc.rccstudios.map.data.network.ApiService
import cc.rccstudios.map.data.network.model.toDto
import cc.rccstudios.map.domain.model.User
import cc.rccstudios.map.domain.repository.SettingsRepository

class UserRepositoryImpl (
    private val apiService: ApiService,
    private val settingsRepository: SettingsRepository
) : cc.rccstudios.map.domain.repository.UserRepository {
    override suspend fun getUser(): Result<Unit> {
        return try {
            val response = apiService.getUser()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    settingsRepository.saveUsername(body.username)
                    if (!body.avatarPath.isNullOrBlank()) settingsRepository.saveAvatarPath(body.avatarPath)
                    if (!body.telegram.isNullOrBlank()) settingsRepository.saveTelegram(body.telegram)
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

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val response = apiService.updateUser(user.toDto())

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}