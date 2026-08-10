package cc.rccstudios.map.domain.usecase

class LoginUseCase (
    private val getTokenUseCase: GetTokenUseCase,
    private val getUserUseCase: GetUserUseCase
) {
    suspend operator fun invoke(): Result<Unit> {
        val getTokenResult = getTokenUseCase()
        if (getTokenResult.isFailure) return getTokenResult
        return getUserUseCase()
    }
}