package cc.rccstudios.map.data.network.model

import cc.rccstudios.map.domain.model.Telemetry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class TelemetryDto(
    @SerialName("token")
    val token: String,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("batteryLevel")
    val batteryPercentage: Int?,
    @SerialName("network")
    val networkType: Int?,
    @SerialName("screenLock")
    val screenLockStatus: Long?,
)

fun Telemetry.toDto() = TelemetryDto(
    token = this.token,
    latitude = this.latitude,
    longitude = this.longitude,
    batteryPercentage = this.batteryPercentage,
    networkType = this.networkStatus ?: 0,
    screenLockStatus = this.screenLockStatus
)