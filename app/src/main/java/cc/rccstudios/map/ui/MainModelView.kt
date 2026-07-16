package cc.rccstudios.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.rccstudios.map.domain.repository.RegisterRepository
import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.domain.usecase.CollectAndSendTelemetryUseCase
import cc.rccstudios.map.domain.usecase.RegisterUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val token: String? = null,
    val backendUrl: String = "",
    val registerKey: String = "",
    val registerName: String = "",
    val isBatteryTrackingEnabled: Boolean = true,
    val isLocationTrackingEnabled: Boolean = true,
    val isNetworkTrackingEnabled: Boolean = true,
    val isScreenLockTrackingEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val logMessage: String = ""
)

class MainModelView(
    private val settingsRepository: SettingsRepository,
    private val registerUseCase: RegisterUseCase,
    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var saveUrlJob: Job? = null
    private var saveKeyJob: Job? = null
    private var saveNameJob: Job? = null

    init {
        viewModelScope.launch {
            val savedToken = settingsRepository.getToken()
            val savedUrl = settingsRepository.getBackendUrl() ?: ""
            val savedRegisterData = settingsRepository.getRegisterData()
            val savedRegisterKey = savedRegisterData.first ?: ""
            val savedRegisterName = savedRegisterData.second ?: ""
            val savedBatteryTrackerEnabled = settingsRepository.getBatteryTrackingEnabled() ?: true
            val savedLocationTrackerEnabled = settingsRepository.getLocationTrackingEnabled() ?: true
            val savedNetworkTrackerEnabled = settingsRepository.getNetworkTrackingEnabled() ?: true
            val savedScreenLockTrackerEnabled = settingsRepository.getScreenLockTrackingEnabled() ?: true

            _uiState.update {
                it.copy(
                    token = savedToken,
                    backendUrl = savedUrl,
                    registerKey = savedRegisterKey,
                    registerName = savedRegisterName,
                    isBatteryTrackingEnabled = savedBatteryTrackerEnabled,
                    isLocationTrackingEnabled = savedLocationTrackerEnabled,
                    isNetworkTrackingEnabled = savedNetworkTrackerEnabled,
                    isScreenLockTrackingEnabled = savedScreenLockTrackerEnabled
                )
            }
        }
    }

    fun onUrlChange(newUrl: String) {
        _uiState.update { it.copy(backendUrl = newUrl) }

        saveUrlJob?.cancel()

        saveUrlJob = viewModelScope.launch {
            delay(1000)
            settingsRepository.saveBackendUrl(newUrl)
            _uiState.update { it.copy(logMessage = "Backend URL saved automatically") }
        }
    }

    fun onKeyChange(newKey: String) {
        _uiState.update { it.copy(registerKey = newKey) }

        saveKeyJob?.cancel()

        saveKeyJob = viewModelScope.launch {
            delay(1000)
            val currentName = _uiState.value.registerName
            settingsRepository.saveRegisterData(
                Pair(
                    newKey,
                    currentName
                )
            )
            _uiState.update { it.copy(logMessage = "Key saved automatically") }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(registerName = newName) }

        saveNameJob?.cancel()

        saveNameJob = viewModelScope.launch {
            delay(1000)
            val currentKey = _uiState.value.registerKey
            settingsRepository.saveRegisterData(
                Pair(
                    currentKey,
                    newName
                )
            )
            _uiState.update { it.copy(logMessage = "Name saved automatically") }
        }
    }

    fun onBatteryTrackingChanged(enabled: Boolean) {
        _uiState.update { it.copy(isBatteryTrackingEnabled = enabled) }

        viewModelScope.launch {
            settingsRepository.saveBatteryTrackingEnabled(enabled)
        }
    }

    fun onLocationTrackingChanged(enabled: Boolean) {
        _uiState.update { it.copy(isLocationTrackingEnabled = enabled) }

        viewModelScope.launch {
            settingsRepository.saveLocationTrackingEnabled(enabled)
        }
    }

    fun onNetworkTrackingChanged(enabled: Boolean) {
        _uiState.update { it.copy(isNetworkTrackingEnabled = enabled) }

        viewModelScope.launch {
            settingsRepository.saveNetworkTrackingEnabled(enabled)
        }
    }

    fun onScreenLockTrackingChanged(enabled: Boolean) {
        _uiState.update { it.copy(isScreenLockTrackingEnabled = enabled) }

        viewModelScope.launch {
            settingsRepository.saveScreenLockTrackingEnabled(enabled)
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, logMessage = "") }
            val result = registerUseCase(
                key = _uiState.value.registerKey,
                name = _uiState.value.registerName
            )
            if (result.isSuccess) {
                val token = settingsRepository.getToken()
                _uiState.update {
                    it.copy(
                        token = token,
                        isLoading = false,
                        logMessage = "Registered successfully"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        logMessage = "Error: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun sendTelemetry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, logMessage = "Sending telemetry") }
            val result = collectAndSendTelemetryUseCase()
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, logMessage = "Telemetry has been sent successfully") }
            } else {
                _uiState.update { it.copy(isLoading = false, logMessage = "Error: ${result.exceptionOrNull()?.message}") }
            }
        }
    }
}