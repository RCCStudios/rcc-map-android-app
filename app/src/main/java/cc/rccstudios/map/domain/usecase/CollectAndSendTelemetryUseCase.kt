package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.repository.TelemetryRepository

class CollectAndSendTelemetryUseCase(
    private val repository: TelemetryRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val telemetry = repository.collectTelemetry()
        return repository.sendTelemetry(telemetry)
    }
}