package cc.rccstudios.map.data.network.model

import cc.rccstudios.map.domain.model.FcmToken
import cc.rccstudios.map.domain.model.Register
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    @SerialName("username")
    val username: String,
    @SerialName("otp")
    val otp: String
)

fun Register.toDto() = RegisterDto(
    username = this.username,
    otp = this.otp
)

@Serializable
data class RegisterResponseDto(
    @SerialName("token")
    val token: String
)

@Serializable
data class GetOtpResponseDto(
    @SerialName("otp")
    val otp: String
)

@Serializable
data class GetTokenResponseDto(
    @SerialName("token")
    val token: String
)

@Serializable
data class FcmTokenDto(
    @SerialName("fcmToken")
    val fcmToken: String
)

fun FcmToken.toDto() = FcmTokenDto(
    fcmToken = this.fcmToken
)