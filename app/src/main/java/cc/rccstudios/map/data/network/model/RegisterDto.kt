package cc.rccstudios.map.data.network.model

import cc.rccstudios.map.domain.model.Register
import cc.rccstudios.map.domain.model.Telemetry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    @SerialName("key")
    val key: String,
    @SerialName("name")
    val name: String
)

fun Register.toDto() = RegisterDto(
    key = this.key,
    name = this.name
)
