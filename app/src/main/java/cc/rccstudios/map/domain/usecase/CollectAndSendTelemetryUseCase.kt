package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.model.Telemetry
import cc.rccstudios.map.domain.repository.TelemetryRepository

class CollectAndSendTelemetryUseCase(
    private val repository: TelemetryRepository
) {
    suspend operator fun invoke(): Result<Telemetry> {
        val telemetry = repository.collectTelemetry()
        return repository.sendTelemetry(telemetry).map { telemetry }
    }
}