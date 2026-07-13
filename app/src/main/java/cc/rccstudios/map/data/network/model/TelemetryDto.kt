package cc.rccstudios.map.data.network.model

import cc.rccstudios.map.domain.model.Telemetry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class TelemetryDto(
    val formattedString: String
)

fun Telemetry.toDto(): TelemetryDto {
    val lat = this.latitude ?: 0.0
    val lon = this.longitude ?: 0.0
    val battery = this.batteryPercentage ?: 0
    val network = this.networkStatus ?: "UNKNOWN"
    val screen = this.screenLockStatus ?: false
    val baseString = "${this.timestamp / 1000} $lat $lon $battery"
    return TelemetryDto(formattedString = baseString)
}

//@Serializable
//data class TelemetryDto(
//    @SerialName("latitude")
//    val latitude: Double?,
//
//    @SerialName("longitude")
//    val longitude: Double?,
//
//    @SerialName("")
//    val batteryPercentage: Int?,
//
//    @SerialName("")
//    val isDeviceLocked: Boolean,
//
//    @SerialName("")
//    val networkType: String?,
//
//    @SerialName("timestamp")
//    val timestamp: Long
//)
