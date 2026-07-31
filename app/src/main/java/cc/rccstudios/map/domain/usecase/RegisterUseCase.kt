package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.model.Register
import cc.rccstudios.map.domain.repository.RegisterRepository

class RegisterUseCase(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(username: String, otp: String): Result<Unit> {
        if (username.isBlank() || otp.isBlank()) {
            return Result.failure(Exception("Cant be blank"))
        }
        val registerData = Register( username, otp)
        return repository.register(registerData)
    }
}