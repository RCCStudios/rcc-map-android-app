package cc.rccstudios.map.domain.repository

import cc.rccstudios.map.domain.model.Register

interface RegisterRepository {
    suspend fun register(): Register
}