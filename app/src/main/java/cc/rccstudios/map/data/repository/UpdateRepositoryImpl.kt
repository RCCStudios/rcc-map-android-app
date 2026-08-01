package cc.rccstudios.map.data.repository

import cc.rccstudios.map.data.network.ApiService
import cc.rccstudios.map.domain.model.UpdateStatus

class UpdateRepositoryImpl(
    private val apiService: ApiService
) : cc.rccstudios.map.domain.repository.UpdateRepository {
    override suspend fun checkUpdates(currentVersion: String): UpdateStatus {
        return try {
            val response = apiService.getLatestRelease()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val latestVersion = body.tagName.removePrefix("v").trim()
                    val currentVersion = currentVersion.removePrefix("v").trim()

                    if (latestVersion != currentVersion) {
                        val downloadUrl = body.assets
                            .firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
                            ?.downloadUrl ?: body.htmlUrl

                        UpdateStatus.NewVersionAvailable(
                            version = body.tagName,
                            downloadUrl = downloadUrl,
                            changelog = body.body ?: ""
                        )
                    } else {
                        UpdateStatus.UpToDate
                    }
                } else {
                    UpdateStatus.Error(Exception("Received null from server"))
                }
            } else {
                UpdateStatus.Error(Exception("HTTP code: ${response.code()}"))
            }
        } catch (e: Exception) {
            UpdateStatus.Error(e)
        }
    }
}