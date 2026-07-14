package cc.rccstudios.map.ui

import cc.rccstudios.map.domain.repository.RegisterRepository
import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.domain.usecase.CollectAndSendTelemetryUseCase
import cc.rccstudios.map.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow

class MainModelView(
    private val settingsRepository: SettingsRepository,
    private val registerRepository: RegisterRepository,
    private val registerUseCase: RegisterUseCase,
    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase
) {
}