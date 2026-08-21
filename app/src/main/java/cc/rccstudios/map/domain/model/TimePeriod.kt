package cc.rccstudios.map.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TimePeriod(
    val id: String = UUID.randomUUID().toString(),
    val startMinuteOfDay: Int,
    val endMinuteOfDay: Int,
    val enabled: Boolean = true
)
