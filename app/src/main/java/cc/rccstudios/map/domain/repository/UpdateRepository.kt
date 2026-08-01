package cc.rccstudios.map.domain.repository

import cc.rccstudios.map.domain.model.UpdateStatus

interface UpdateRepository {
    suspend fun checkUpdates(currentVersion: String): UpdateStatus
}