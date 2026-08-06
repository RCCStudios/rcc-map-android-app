package cc.rccstudios.map.domain.repository

import cc.rccstudios.map.domain.model.User

interface UserRepository {
    suspend fun getUser(): Result<Unit>
    suspend fun updateUser(user: User): Result<Unit>
}