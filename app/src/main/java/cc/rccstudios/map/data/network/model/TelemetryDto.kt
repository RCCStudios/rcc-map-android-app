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
    val batteryLevel: Int?, // batteryPercentage
    @SerialName("network")
    val network: Int?, // networkStatus
    @SerialName("screenLock")
    val screenLock: Long?, // screenLockStatus
)

fun Telemetry.toDto() = TelemetryDto(
    latitude = this.latitude,
    longitude = this.longitude,
    batteryLevel = this.batteryPercentage,
    network = this.networkStatus ?: 0,
    screenLock = this.screenLockStatus
)