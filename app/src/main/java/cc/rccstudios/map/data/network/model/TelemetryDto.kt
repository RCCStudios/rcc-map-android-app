package cc.rccstudios.map.data.network.model

import cc.rccstudios.map.domain.model.Telemetry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TelemetryDto(
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("batteryLevel")
    val batteryLevel: Int?, // batteryPercentage
    @SerialName("network")
    val network: Int?, // networkStatus
    @SerialName("screenLock")
    val screenLock: Long?, // screenLockStatus
)

fun Telemetry.toDto() = TelemetryDto(
    latitude = this.latitude,
    longitude = this.longitude,
    batteryLevel = this.batteryStatus,
    network = this.networkStatus ?: 0,
    screenLock = this.screenLockStatus
)