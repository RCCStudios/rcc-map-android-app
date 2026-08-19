package cc.rccstudios.map.data.repository

import cc.rccstudios.map.data.network.ApiService
import cc.rccstudios.map.data.network.model.toDto
import cc.rccstudios.map.domain.model.Device
import cc.rccstudios.map.domain.repository.DeviceRepository

class DeviceRepositoryImpl(
    private val apiService: ApiService
) : DeviceRepository {
    override suspend fun updateDevice(device: Device): Result<Unit> {
        return try {
            val response = apiService.updateDevice(device.toDto())

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}