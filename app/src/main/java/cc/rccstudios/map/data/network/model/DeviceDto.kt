package cc.rccstudios.map.data.network.model

import cc.rccstudios.map.domain.model.Device
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDto(
    @SerialName("fid")
    val fid: String,
    @SerialName("version")
    val version: String
)

fun Device.toDto() = DeviceDto(
    fid = this.fid,
    version = this.version
)