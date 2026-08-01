package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.model.UpdateStatus
import cc.rccstudios.map.domain.repository.UpdateRepository

class CheckUpdatesUseCase(
    private val updateRepository: UpdateRepository
) {
    suspend operator fun invoke(currentVersion: String): UpdateStatus {
        return updateRepository.checkUpdates(currentVersion)
    }
}