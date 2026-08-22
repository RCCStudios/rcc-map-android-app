package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.utils.isSilenceNow

class ShouldSuppressBomberUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): Boolean {
        val bomberEnabled = settingsRepository.getBomberEnabled()
        val periods = settingsRepository.getBomberSilencePeriods()
        return periods.isSilenceNow() || !bomberEnabled
    }
}