package cc.rccstudios.map.data.network.model

import cc.rccstudios.map.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUserResponseDto(
    @SerialName("username")
    val username: String,
    @SerialName("avatarPath")
    val avatarPath: String?,
    @SerialName("telegram")
    val telegram: String?
)

@Serializable
data class UpdateUserDto(
    @SerialName("username")
    val username: String,
    @SerialName("avatarPath")
    val avatarPath: String,
    @SerialName("telegram")
    val telegram: String
)

fun User.toDto() = UpdateUserDto(
    username = this.username,
    avatarPath = this.avatarPath,
    telegram = this.telegram
)