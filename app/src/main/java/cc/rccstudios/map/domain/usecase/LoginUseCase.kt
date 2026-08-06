package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.repository.UserRepository

class LoginUseCase (
    private val getTokenUseCase: GetTokenUseCase,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val getTokenResult = getTokenUseCase()
        if (getTokenResult.isFailure) return getTokenResult
        return userRepository.getUser()
    }
}