package cc.rccstudios.map.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetOtpResponseDto(
    @SerialName("otp")
    val otp: String
)