package cc.rccstudios.map.data.network.model

import cc.rccstudios.map.domain.model.Fid
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
data class FidDto(
    @SerialName("fid")
    val fid: String
)

fun Fid.toDto() = FidDto(
    fid = this.fid
)