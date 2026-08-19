package cc.rccstudios.map.domain.usecase

import cc.rccstudios.map.BuildConfig
import cc.rccstudios.map.domain.model.Device
import cc.rccstudios.map.domain.repository.DeviceRepository
import cc.rccstudios.map.domain.repository.SettingsRepository

class UpdateDeviceUseCase(
    private val settingsRepository: SettingsRepository,
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(fid: String?): Result<Unit> {
        if (fid.isNullOrBlank()) {
            return Result.failure(Exception("fid can't be blank"))
        }
        settingsRepository.saveFid(fid)
        return deviceRepository.updateDevice(
            Device(
                fid = fid,
                version = BuildConfig.VERSION_NAME
            )
        )
    }
}