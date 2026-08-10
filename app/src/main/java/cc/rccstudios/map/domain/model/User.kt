package cc.rccstudios.map.domain.model

data class User(
    val username: String,
    val avatar: ByteArray?,
    val telegram: String?
)