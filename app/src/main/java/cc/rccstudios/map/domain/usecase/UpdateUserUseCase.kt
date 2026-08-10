package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.model.User
import cc.rccstudios.map.domain.repository.UserRepository

class UpdateUserUseCase (
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        username: String,
        avatar: ByteArray? = null,
        telegram: String? = null
    ): Result<Unit> {
        val userData = User(username, avatar, telegram)
        return userRepository.updateUser(userData)
    }
}