package cc.rccstudios.map.domain.model

data class Telemetry(
    val latitude: Double?,
    val longitude: Double?,
    val batteryPercentage: Int?,
    val networkStatus: String?,
    val screenLockStatus: Boolean,
    val timestamp: Long
)
