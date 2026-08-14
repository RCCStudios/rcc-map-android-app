package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.model.Register
import cc.rccstudios.map.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, otp: String): Result<Unit> {
        if (username.isBlank() || otp.isBlank()) {
            return Result.failure(Exception("registerData can't be blank"))
        }
        val registerData = Register(username, otp)
        return authRepository.register(registerData)
    }
}