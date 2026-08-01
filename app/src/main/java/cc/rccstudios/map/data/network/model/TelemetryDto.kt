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
    @SerialName("batteryStatus")
    val batteryStatus: Int?,
    @SerialName("networkStatus")
    val networkStatus: Int?,
    @SerialName("screenLockStatus")
    val screenLockStatus: Boolean?
)

fun Telemetry.toDto() = TelemetryDto(
    latitude = this.latitude,
    longitude = this.longitude,
    batteryStatus = this.batteryStatus,
    networkStatus = this.networkStatus ?: 0,
    screenLockStatus = this.screenLockStatus
)