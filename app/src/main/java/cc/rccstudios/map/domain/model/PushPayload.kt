package cc.rccstudios.map.domain.model

data class PushPayload(
    val action: String?,
    val title: String?,
    val body: String?,
    val data: Map<String, String>
)