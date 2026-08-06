package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.model.Telemetry
import cc.rccstudios.map.domain.repository.TelemetryRepository

class CollectAndSendTelemetryUseCase(
    private val telemetryRepository: TelemetryRepository
) {
    suspend operator fun invoke(): Result<Telemetry> {
        val telemetry = telemetryRepository.collectTelemetry()
        return telemetryRepository.sendTelemetry(telemetry).map { telemetry }
    }
}