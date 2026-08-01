package cc.rccstudios.map.domain.model

sealed interface UpdateStatus {
    data class NewVersionAvailable(
        val version: String,
        val downloadUrl: String,
        val changelog: String
    ) : UpdateStatus

    data object UpToDate : UpdateStatus
    data class Error(val throwable: Throwable) : UpdateStatus
}