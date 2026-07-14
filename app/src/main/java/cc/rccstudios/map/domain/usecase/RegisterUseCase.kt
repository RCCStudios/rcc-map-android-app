package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.repository.RegisterRepository

class RegisterUseCase(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val registerData = repository.getRegisterData()
        return repository.register(registerData)
    }
}