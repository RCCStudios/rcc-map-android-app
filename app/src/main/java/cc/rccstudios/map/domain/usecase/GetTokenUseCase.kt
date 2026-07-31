package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.repository.AuthRepository

class GetTokenUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.getToken()
    }
}