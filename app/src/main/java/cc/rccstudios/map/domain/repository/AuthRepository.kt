package cc.rccstudios.map.domain.repository

import cc.rccstudios.map.domain.model.FcmToken
import cc.rccstudios.map.domain.model.Register

interface AuthRepository {
    suspend fun register(register: Register): Result<Unit>
    suspend fun getOtp(): Result<Unit>
    suspend fun getToken(): Result<Unit>
    suspend fun updateFcmToken(fcmToken: FcmToken): Result<Unit>
}