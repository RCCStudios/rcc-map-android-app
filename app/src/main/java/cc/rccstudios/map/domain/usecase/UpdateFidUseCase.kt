package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.model.Fid
import cc.rccstudios.map.domain.repository.AuthRepository
import cc.rccstudios.map.domain.repository.SettingsRepository

class UpdateFidUseCase(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(fid: String?): Result<Unit> {
        if (fid.isNullOrBlank()) {
            return Result.failure(Exception("fid can't be blank"))
        }
        settingsRepository.fidToken(fid)
        return authRepository.updateFid(Fid(fid))
    }
}