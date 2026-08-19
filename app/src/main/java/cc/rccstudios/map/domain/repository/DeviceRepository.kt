package cc.rccstudios.map.domain.repository

import cc.rccstudios.map.domain.model.Device

interface DeviceRepository {
    suspend fun updateDevice(device: Device): Result<Unit>
}