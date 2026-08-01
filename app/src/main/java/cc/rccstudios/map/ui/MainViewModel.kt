package cc.rccstudios.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.rccstudios.map.BuildConfig
import cc.rccstudios.map.domain.model.UpdateStatus
import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.domain.usecase.CheckUpdatesUseCase
import cc.rccstudios.map.domain.usecase.CollectAndSendTelemetryUseCase
import cc.rccstudios.map.domain.usecase.GetOtpUseCase
import cc.rccstudios.map.domain.usecase.GetTokenUseCase
import cc.rccstudios.map.domain.usecase.RegisterUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode(val code: Int) {
    REGISTER(0),
    LOGIN(1),
    LOGGED_IN(2);

    companion object {
        fun fromCode(code: Int?): AuthMode {
            return entries.find { it.code == code } ?: REGISTER
        }
    }
}

data class UiState(
    val token: String? = null,
    val serverUrl: String = "",
    val username: String = "",
    val otp: String? = null,
    val authMode: AuthMode = AuthMode.REGISTER,
    val avatarPath: String = "",
    val isBatteryTrackingEnabled: Boolean = true,
    val isLocationTrackingEnabled: Boolean = true,
    val isNetworkTrackingEnabled: Boolean = true,
    val isScreenLockTrackingEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val logMessage: String = "",
    val updateInfo: UpdateStatus? = UpdateStatus.UpToDate
)

class MainViewModel(
    private val settingsRepository: SettingsRepository,
    private val registerUseCase: RegisterUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val getOtpUseCase: GetOtpUseCase,
    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase,
    private val checkUpdatesUseCase: CheckUpdatesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var saveUrlJob: Job? = null
    private var saveOtpJob: Job? = null
    private var saveUsernameJob: Job? = null

    init {
        viewModelScope.launch {
            val savedToken = settingsRepository.getToken()
            val savedUrl = settingsRepository.getServerUrl() ?: ""
            val savedOtp = settingsRepository.getOtp()
            val savedUsername = settingsRepository.getUsername() ?: ""
            val savedAuthCode = settingsRepository.getAuthMode()
            val currentAuthMode = if (!savedToken.isNullOrBlank()) {
                AuthMode.LOGGED_IN
            } else {
                AuthMode.fromCode(savedAuthCode)
            }
            val savedAvatarPath = settingsRepository.getAvatarPath() ?: ""
            val savedBatteryTrackerEnabled = settingsRepository.getBatteryTrackingEnabled() ?: true
            val savedLocationTrackerEnabled = settingsRepository.getLocationTrackingEnabled() ?: true
            val savedNetworkTrackerEnabled = settingsRepository.getNetworkTrackingEnabled() ?: true
            val savedScreenLockTrackerEnabled = settingsRepository.getScreenLockTrackingEnabled() ?: true

            _uiState.update {
                it.copy(
                    token = savedToken,
                    serverUrl = savedUrl,
                    otp = savedOtp,
                    username = savedUsername,
                    authMode = currentAuthMode,
                    avatarPath = savedAvatarPath,
                    isBatteryTrackingEnabled = savedBatteryTrackerEnabled,
                    isLocationTrackingEnabled = savedLocationTrackerEnabled,
                    isNetworkTrackingEnabled = savedNetworkTrackerEnabled,
                    isScreenLockTrackingEnabled = savedScreenLockTrackerEnabled
                )
            }
        }
    }

    fun onUrlChange(newUrl: String) {
        _uiState.update { it.copy(serverUrl = newUrl) }

        saveUrlJob?.cancel()

        saveUrlJob = viewModelScope.launch {
            delay(1000L)
            settingsRepository.saveServerUrl(newUrl)
            _uiState.update { it.copy(logMessage = "Server URL saved automatically") }
        }
    }

    fun onUsernameChange(newUsername: String) {
        _uiState.update { it.copy(username = newUsername) }

        saveUsernameJob?.cancel()

        saveUsernameJob = viewModelScope.launch {
            delay(1000L)
            settingsRepository.saveUsername(newUsername)
            _uiState.update { it.copy(logMessage = "Username saved automatically") }
        }
    }

    fun onOtpChange(newOtp: String) {
        _uiState.update { it.copy(otp = newOtp) }

        saveOtpJob?.cancel()

        saveOtpJob = viewModelScope.launch {
            delay(1000L)
            settingsRepository.saveOtp(newOtp)
            _uiState.update { it.copy(logMessage = "OTP saved automatically") }
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
                username = _uiState.value.username,
                otp = _uiState.value.otp ?: ""
            )
            if (result.isSuccess) {
                val token = settingsRepository.getToken()
                _uiState.update {
                    it.copy(
                        token = token,
                        isLoading = false,
                        authMode = AuthMode.LOGGED_IN,
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

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, logMessage = "") }
            val result = getTokenUseCase()
            if (result.isSuccess) {
                val token = settingsRepository.getToken()
                _uiState.update {
                    it.copy(
                        token = token,
                        authMode = AuthMode.LOGGED_IN,
                        isLoading = false,
                        logMessage = "Logged in successfully"
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

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, logMessage = "") }
            settingsRepository.saveToken("")
            settingsRepository.saveUsername("")
            settingsRepository.saveOtp("")
            _uiState.update {
                it.copy(
                    token = "",
                    username = "",
                    otp = "",
                    authMode = AuthMode.REGISTER,
                    isLoading = false,
                    logMessage = "Logged out successfully"
                )
            }
        }
    }

    fun getOtp() {
        viewModelScope.launch {
            _uiState.update { it.copy(logMessage = "") }
            val result = getOtpUseCase()
            if (result.isSuccess) {
                val otp = settingsRepository.getOtp()
                _uiState.update {
                    it.copy(
                        otp = otp,
                        isLoading = false,
                        logMessage = "Received OTP successfully"
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

    fun toggleAuthMode() {
        viewModelScope.launch {
            _uiState.update { it.copy(logMessage = "") }
            val authMode = AuthMode.fromCode(settingsRepository.getAuthMode())
            val nextMode = if (authMode == AuthMode.REGISTER) {
                AuthMode.LOGIN
            } else {
                AuthMode.REGISTER
            }
            settingsRepository.saveAuthMode(nextMode.code)
            _uiState.update { it.copy(authMode = nextMode) }
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

    fun checkUpdates() {
        _uiState.update { it.copy(isLoading = true, logMessage = "Checking updates") }
        val currentVersion = BuildConfig.VERSION_NAME
        viewModelScope.launch {
            when (val result = checkUpdatesUseCase(currentVersion)) {
                is UpdateStatus.NewVersionAvailable -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            updateInfo = result,
                            logMessage = "Found new version: ${result.version}"
                        )
                    }
                }

                is UpdateStatus.UpToDate -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            logMessage = "You have the latest version"
                        )
                    }
                }

                is UpdateStatus.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            logMessage = "Failed to check updates: ${result.throwable.message}"
                        )
                    }
                }
            }
        }
    }

    fun dismissUpdateDialog() {
        _uiState.update { it.copy(updateInfo = null) }
    }
}