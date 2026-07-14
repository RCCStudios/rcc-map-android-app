package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.model.Register
import cc.rccstudios.map.domain.repository.RegisterRepository

class RegisterUseCase(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(key: String, name: String): Result<Unit> {
        if (key.isBlank() || name.isBlank()) {
            return Result.failure(Exception("Cant be blank"))
        }
        val registerData = Register(key, name)
        return repository.register(registerData)
    }
}