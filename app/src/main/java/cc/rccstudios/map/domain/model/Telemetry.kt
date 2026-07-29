package cc.rccstudios.map.domain.model

data class Telemetry(
    val latitude: Double?,
    val longitude: Double?,
    val batteryStatus: Int?,
    val networkStatus: Int?,
    val screenLockStatus: Long?
)
