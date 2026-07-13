package cc.rccstudios.map.domain.repository

import cc.rccstudios.map.domain.model.Telemetry

interface TelemetryRepository {
    suspend fun collectTelemetry(): Telemetry
//    suspend fun saveTelemetryToDB(telemetry: Telemetry): Boolean
//    suspend fun getTelemetryFromDB(): Telemetry
//    suspend fun removeTelemetryFromDB(): Boolean
    suspend fun sendTelemetry(telemetry: Telemetry): Result<Unit>
}