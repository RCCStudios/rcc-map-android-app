package cc.rccstudios.map.domain.repository

import cc.rccstudios.map.domain.model.Register

interface RegisterRepository {
//    suspend fun getRegisterData(): Register?
    suspend fun register(register: Register): Result<Unit>
}