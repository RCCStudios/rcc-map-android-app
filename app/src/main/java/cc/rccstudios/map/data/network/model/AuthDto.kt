package cc.rccstudios.map.data.network.model

import cc.rccstudios.map.domain.model.Register
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    @SerialName("username")
    val name: String, // username
    @SerialName("otp")
    val key: String // key
)

fun Register.toDto() = RegisterDto(
    name = this.username,
    key = this.otp
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