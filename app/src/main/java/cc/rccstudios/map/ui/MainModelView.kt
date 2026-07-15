package cc.rccstudios.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.rccstudios.map.domain.repository.RegisterRepository
import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.domain.usecase.CollectAndSendTelemetryUseCase
import cc.rccstudios.map.domain.usecase.RegisterUseCase
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
    val isLoading: Boolean = false,
    val logMessage: String = ""
)

class MainModelView(
    private val settingsRepository: SettingsRepository,
    private val registerRepository: RegisterRepository,
    private val registerUseCase: RegisterUseCase,
    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val savedToken = settingsRepository.getToken()
            val savedUrl = settingsRepository.getBackendUrl() ?: ""

            _uiState.update {
                it.copy(
                    token = savedToken,
                    backendUrl = savedUrl
                )
            }
        }
    }

    fun onUrlChange(newUrl: String) {
        _uiState.update { it.copy(backendUrl = newUrl) }
    }

    fun onKeyChange(newKey: String) {
        _uiState.update { it.copy(registerKey = newKey) }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(registerName = newName) }
    }

    fun saveUrl() {
        viewModelScope.launch {
            settingsRepository.saveBackendUrl(_uiState.value.backendUrl)
            _uiState.update { it.copy(logMessage = "Backend url was saved successfully") }
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