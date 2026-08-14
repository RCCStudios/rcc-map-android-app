package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.model.FcmToken
import cc.rccstudios.map.domain.repository.AuthRepository
import cc.rccstudios.map.domain.repository.SettingsRepository

class UpdateFcmTokenUseCase(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(fcmToken: String?): Result<Unit> {
        if (fcmToken.isNullOrBlank()) {
            return Result.failure(Exception("fcmToken can't be blank"))
        }
        settingsRepository.saveFcmToken(fcmToken)
        return authRepository.updateFcmToken(FcmToken(fcmToken))
    }
}