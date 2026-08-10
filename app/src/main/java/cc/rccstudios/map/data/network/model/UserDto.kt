package cc.rccstudios.map.data.network.model

import android.util.Base64
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
    @SerialName("avatar")
    val avatar: String?,
    @SerialName("telegram")
    val telegram: String?
)

fun User.toDto() = UpdateUserDto(
    username = this.username,
    avatar = this.avatar?.let { bytes ->
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    },
    telegram = this.telegram
)