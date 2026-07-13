package cc.rccstudios.map.domain.model

data class Telemetry(
    val token: String,
    val state: Int,
    val latitude: Double?,
    val longitude: Double?,
    val batteryPercentage: Int?,
    val networkStatus: Int?,
    val screenLockStatus: Any
)
