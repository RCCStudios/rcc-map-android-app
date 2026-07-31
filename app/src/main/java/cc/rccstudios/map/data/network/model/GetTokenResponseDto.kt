package cc.rccstudios.map.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetTokenResponseDto(
    @SerialName("token")
    val token: String
)
