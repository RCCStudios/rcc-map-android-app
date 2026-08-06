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
import cc.rccstudios.map.domain.usecase.LoginUseCase
import cc.rccstudios.map.domain.usecase.RegisterUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
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
    val telemetryInterval: Long = 60000L,
    val isLoading: Boolean = false,
    val logMessage: String = "",
    val updateInfo: UpdateStatus? = UpdateStatus.UpToDate
)

@OptIn(FlowPreview::class)
class MainViewModel(
    private val settingsRepository: SettingsRepository,
    private val registerUseCase: RegisterUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val loginUseCase: LoginUseCase,
    private val getOtpUseCase: GetOtpUseCase,
    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase,
    private val checkUpdatesUseCase: CheckUpdatesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val urlInputFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val usernameInputFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val otpInputFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val intervalInputFlow = MutableSharedFlow<Long>(extraBufferCapacity = 1)

    init {
        observeSettings()
        setupDebounceAutoSave()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            combine(settingsRepository.tokenFlow, settingsRepository.authModeFlow) { token, authCode ->
                val authMode = if (!token.isNullOrBlank()) {
                    AuthMode.LOGGED_IN
                } else {
                    AuthMode.fromCode(authCode)
                }
                token to authMode
            }.collect { (token, authMode) ->
                _uiState.update { it.copy(token = token, authMode = authMode) }
            }
        }

        viewModelScope.launch {
            settingsRepository.serverUrlFlow.collect { url ->
                if (url != null) _uiState.update { it.copy(serverUrl = url) }
            }
        }
        viewModelScope.launch {
            settingsRepository.usernameFlow.collect { username ->
                if (username != null) _uiState.update { it.copy(username = username) }
            }
        }
        viewModelScope.launch {
            settingsRepository.otpFlow.collect { otp ->
                _uiState.update { it.copy(otp = otp) }
            }
        }
        viewModelScope.launch {
            settingsRepository.avatarPathFlow.collect { path ->
                if (path != null) _uiState.update { it.copy(avatarPath = path) }
            }
        }

        viewModelScope.launch {
            combine(
                settingsRepository.batteryTrackingEnabledFlow,
                settingsRepository.locationTrackingEnabledFlow,
                settingsRepository.networkTrackingEnabledFlow,
                settingsRepository.screenLockTrackingEnabledFlow,
                settingsRepository.telemetryIntervalFlow
            ) { battery, location, network, screenLock, interval ->
                _uiState.update {
                    it.copy(
                        isBatteryTrackingEnabled = battery,
                        isLocationTrackingEnabled = location,
                        isNetworkTrackingEnabled = network,
                        isScreenLockTrackingEnabled = screenLock,
                        telemetryInterval = interval
                    )
                }
            }.collect()
        }
    }

    private fun setupDebounceAutoSave() {
        viewModelScope.launch {
            urlInputFlow
                .debounce(1000L)
                .collect { url ->
                    settingsRepository.saveServerUrl(url)
                    _uiState.update { it.copy(logMessage = "Server URL saved automatically") }
                }
        }

        viewModelScope.launch {
            usernameInputFlow
                .debounce(1000L)
                .collect { username ->
                    settingsRepository.saveUsername(username)
                    _uiState.update { it.copy(logMessage = "Username saved automatically") }
                }
        }

        viewModelScope.launch {
            otpInputFlow
                .debounce(1000L)
                .collect { otp ->
                    settingsRepository.saveOtp(otp)
                    _uiState.update { it.copy(logMessage = "OTP saved automatically") }
                }
        }

        viewModelScope.launch {
            intervalInputFlow
                .debounce(1000L)
                .collect { interval ->
                    settingsRepository.saveTelemetryInterval(interval)
                    _uiState.update { it.copy(logMessage = "Telemetry interval saved automatically") }
                }
        }
    }

    fun onUrlChange(newUrl: String) {
        _uiState.update { it.copy(serverUrl = newUrl) }
        urlInputFlow.tryEmit(newUrl)
    }

    fun onUsernameChange(newUsername: String) {
        _uiState.update { it.copy(username = newUsername) }
        usernameInputFlow.tryEmit(newUsername)
    }

    fun onOtpChange(newOtp: String) {
        _uiState.update { it.copy(otp = newOtp) }
        otpInputFlow.tryEmit(newOtp)
    }

    fun onTelemetryIntervalChange(interval: Long) {
        _uiState.update { it.copy(telemetryInterval = interval) }
        intervalInputFlow.tryEmit(interval)
    }

    fun onBatteryTrackingChange(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.saveBatteryTrackingEnabled(enabled) }
    }

    fun onLocationTrackingChange(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.saveLocationTrackingEnabled(enabled) }
    }

    fun onNetworkTrackingChange(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.saveNetworkTrackingEnabled(enabled) }
    }

    fun onScreenLockTrackingChange(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.saveScreenLockTrackingEnabled(enabled) }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, logMessage = "") }
            val result = registerUseCase(
                username = _uiState.value.username,
                otp = _uiState.value.otp ?: ""
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
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

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, logMessage = "") }
            val result = loginUseCase()
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
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
                _uiState.update {
                    it.copy(
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
            val currentAuthMode = _uiState.value.authMode
            val nextMode = if (currentAuthMode == AuthMode.REGISTER) {
                AuthMode.LOGIN
            } else {
                AuthMode.REGISTER
            }
            settingsRepository.saveAuthMode(nextMode.code)
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