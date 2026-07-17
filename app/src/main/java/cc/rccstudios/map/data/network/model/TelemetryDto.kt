package cc.rccstudios.map.data.network.model

import cc.rccstudios.map.domain.model.Telemetry
import com.google.android.gms.common.api.Status
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
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("batteryLevel")
    val batteryPercentage: Int?,
    @SerialName("network")
    val networkStatus: Int?,
    @SerialName("screenLock")
    val screenLockStatus: Long?,
)

fun Telemetry.toDto() = TelemetryDto(
    latitude = this.latitude,
    longitude = this.longitude,
    batteryPercentage = this.batteryPercentage,
    networkStatus = this.networkStatus ?: 0,
    screenLockStatus = this.screenLockStatus
)