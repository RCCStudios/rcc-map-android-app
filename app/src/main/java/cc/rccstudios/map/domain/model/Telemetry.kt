package cc.rccstudios.map.domain.model

data class Telemetry(
    val latitude: Float,
    val longitude: Float,
    val batteryPercentage: Int,
    val networkStatus: String,
    val deviceLockedStatus: Long,
    val timestamp: Long
)
