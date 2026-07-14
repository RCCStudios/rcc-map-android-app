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

object ScreenLockSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ScreenLock", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw java.lang.IllegalStateException("This serializer can only be used with Json")

        when (value) {
            is Boolean -> jsonEncoder.encodeJsonElement(JsonPrimitive(value))
            is Long -> jsonEncoder.encodeJsonElement(JsonPrimitive(value))
            is Int -> jsonEncoder.encodeJsonElement(JsonPrimitive(value.toLong()))
            else -> jsonEncoder.encodeJsonElement(JsonPrimitive(false))
        }
    }

    override fun deserialize(decoder: Decoder): Any {
        return false
    }
}

@Serializable
data class TelemetryDto(
    @SerialName("token")
    val token: String,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("battery")
    val batteryPercentage: Int?,
    @SerialName("network")
    val networkType: Int?,
    @SerialName("screenlock")
    @Serializable(with = ScreenLockSerializer::class)
    val screenLockStatus: Any,
)

fun Telemetry.toDto() = TelemetryDto(
    token = this.token,
    latitude = this.latitude,
    longitude = this.longitude,
    batteryPercentage = this.batteryPercentage,
    networkType = this.networkStatus ?: 0,
    screenLockStatus = this.screenLockStatus
)