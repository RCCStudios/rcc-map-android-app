package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.domain.model.Telemetry
import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.domain.repository.TelemetryRepository
import kotlinx.coroutines.flow.first

class CollectAndSendTelemetryUseCase(
    private val settingsRepository: SettingsRepository,
    private val telemetryRepository: TelemetryRepository
) {
    suspend operator fun invoke(): Result<Telemetry> {
        val isEnabled = settingsRepository.telemetryEnabledFlow.first()
        if (!isEnabled) {
            Result.success(Unit)
        }
        val telemetry = telemetryRepository.collectTelemetry()
        return telemetryRepository.sendTelemetry(telemetry).map { telemetry }
    }
}