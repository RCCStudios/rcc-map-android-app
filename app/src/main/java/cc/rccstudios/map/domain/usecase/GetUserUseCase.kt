package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return userRepository.getUser()
    }
}