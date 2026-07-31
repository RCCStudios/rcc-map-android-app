package cc.rccstudios.map.domain.repository

import cc.rccstudios.map.domain.model.Telemetry

interface TelemetryRepository {
    suspend fun collectTelemetry(): Telemetry
    suspend fun sendTelemetry(telemetry: Telemetry): Result<Unit>
}