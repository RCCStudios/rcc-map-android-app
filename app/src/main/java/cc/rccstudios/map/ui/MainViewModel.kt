package cc.rccstudios.map.ui

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.rccstudios.map.BuildConfig
import cc.rccstudios.map.R
import cc.rccstudios.map.domain.model.TimePeriod
import cc.rccstudios.map.domain.model.UpdateStatus
import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.domain.usecase.CheckUpdatesUseCase
import cc.rccstudios.map.domain.usecase.CollectAndSendTelemetryUseCase
import cc.rccstudios.map.domain.usecase.GetOtpUseCase
import cc.rccstudios.map.domain.usecase.GetUserUseCase
import cc.rccstudios.map.domain.usecase.LoginUseCase
import cc.rccstudios.map.domain.usecase.RegisterUseCase
import cc.rccstudios.map.domain.usecase.UpdateUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

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
    val telegram: String = "",
    val telemetryEnabled: Boolean = true,
    val batteryTrackingEnabled: Boolean = true,
    val locationTrackingEnabled: Boolean = true,
    val networkTrackingEnabled: Boolean = true,
    val screenLockTrackingEnabled: Boolean = true,
    val telemetryInterval: Long = 60000L,
    val bomberEnabled: Boolean = true,
    val bomberSilencePeriods: List<TimePeriod> = emptyList(),
    val bomberSoundId: Int = R.raw.bomber_alarm_1,
    val isLoading: Boolean = false,
    val isSoundPreviewPlaying: Boolean = false,
    val logMessage: String = "",
    val updateInfo: UpdateStatus? = UpdateStatus.UpToDate
)

@OptIn(FlowPreview::class)
class MainViewModel(
    private val settingsRepository: SettingsRepository,
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val getOtpUseCase: GetOtpUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase,
    private val checkUpdatesUseCase: CheckUpdatesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    private val urlInputFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val usernameInputFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val otpInputFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val intervalInputFlow = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    private val telegramInputFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)

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
            settingsRepository.telegramFlow.collect { telegram ->
                if (telegram != null) _uiState.update { it.copy(telegram = telegram) }
            }
        }

        viewModelScope.launch {
            combine(
                settingsRepository.telemetryEnabledFlow,
                settingsRepository.batteryTrackingEnabledFlow,
                settingsRepository.locationTrackingEnabledFlow,
                settingsRepository.networkTrackingEnabledFlow,
                settingsRepository.screenLockTrackingEnabledFlow
            ) { telemetry, battery, location, network, screenLock ->
                val allDisabled = !battery && !location && !network && !screenLock
                val effectiveTelemetry = if (allDisabled) false else telemetry

                if (allDisabled && telemetry) {
                    viewModelScope.launch {
                        settingsRepository.saveTelemetryEnabled(false)
                    }
                }

                _uiState.update {
                    it.copy(
                        telemetryEnabled = effectiveTelemetry,
                        batteryTrackingEnabled = battery,
                        locationTrackingEnabled = location,
                        networkTrackingEnabled = network,
                        screenLockTrackingEnabled = screenLock
                    )
                }
            }.collect()
        }

        viewModelScope.launch {
            settingsRepository.telemetryIntervalFlow.collect { interval ->
                _uiState.update { it.copy(telemetryInterval = interval) }
            }
        }

        viewModelScope.launch {
            combine(
                settingsRepository.bomberEnabledFlow,
                settingsRepository.bomberSilencePeriodsFlow,
                settingsRepository.bomberSoundIdFlow
            ) { bomberEnabled, bomberSilencePeriods, bomberSoundId ->
                _uiState.update {
                    it.copy(
                        bomberEnabled = bomberEnabled,
                        bomberSilencePeriods = bomberSilencePeriods,
                        bomberSoundId = bomberSoundId
                    )
                }
            }.collect()
        }
    }

    private fun setupDebounceAutoSave() {
        viewModelScope.launch {
            urlInputFlow
                .debounce(1000L.milliseconds)
                .collect { url ->
                    settingsRepository.saveServerUrl(url)
                    _uiState.update { it.copy(logMessage = "Server URL saved automatically") }
                }
        }

        viewModelScope.launch {
            usernameInputFlow
                .debounce(1000L.milliseconds)
                .collect { username ->
                    settingsRepository.saveUsername(username)
                    _uiState.update { it.copy(logMessage = "Username saved automatically") }
                }
        }

        viewModelScope.launch {
            otpInputFlow
                .debounce(1000L.milliseconds)
                .collect { otp ->
                    settingsRepository.saveOtp(otp)
                    _uiState.update { it.copy(logMessage = "OTP saved automatically") }
                }
        }

        viewModelScope.launch {
            intervalInputFlow
                .debounce(1000L.milliseconds)
                .collect { interval ->
                    settingsRepository.saveTelemetryInterval(interval)
                    _uiState.update { it.copy(logMessage = "Telemetry interval saved automatically") }
                }
        }

        viewModelScope.launch {
            telegramInputFlow
                .debounce(1000L.milliseconds)
                .collect { telegram ->
                    settingsRepository.saveTelegram(telegram)
                    _uiState.update { it.copy(logMessage = "Telegram saved automatically") }
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

    fun onTelemetryEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                val state = _uiState.value
                val allDisabled = !state.batteryTrackingEnabled &&
                        !state.locationTrackingEnabled &&
                        !state.networkTrackingEnabled &&
                        !state.screenLockTrackingEnabled

                if (allDisabled) {
                    settingsRepository.saveBatteryTrackingEnabled(true)
                    settingsRepository.saveLocationTrackingEnabled(true)
                    settingsRepository.saveNetworkTrackingEnabled(true)
                    settingsRepository.saveScreenLockTrackingEnabled(true)
                }
            }
            settingsRepository.saveTelemetryEnabled(enabled)
        }
    }

    fun onTelegramChange(newTelegram: String) {
        _uiState.update { it.copy(telegram = newTelegram) }
        telegramInputFlow.tryEmit(newTelegram)
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

    fun onBomberEnabledChange(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.saveBomberEnabled(enabled) }
    }

    fun addBomberSilencePeriod(period: TimePeriod) {
        viewModelScope.launch { settingsRepository.addBomberSilencePeriod(period) }
    }

    fun removeBomberSilencePeriod(id: String) {
        viewModelScope.launch { settingsRepository.removeBomberSilencePeriod(id) }
    }

    fun updateBomberSilencePeriod(period: TimePeriod) {
        viewModelScope.launch { settingsRepository.updateBomberSilencePeriod(period) }
    }

    fun onBomberSoundIdChange(@IdRes id: Int) {
        stopSoundPreview()
        _uiState.update { it.copy(bomberSoundId = id) }
        viewModelScope.launch { settingsRepository.savebomberSoundId(id) }
    }

    fun updateUser(
        context: Context? = null,
        imageUri: Uri? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val bytes = if (context != null && imageUri != null) {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                }
            } else null
            val result = updateUserUseCase(
                username = _uiState.value.username,
                avatar = bytes,
                telegram = _uiState.value.telegram
            )
            if (result.isSuccess) {
                val getUserResult = getUserUseCase()
                if (getUserResult.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            logMessage = "Updated user successfully"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            logMessage = "Updated user successfully, error while getting user: ${result.exceptionOrNull()?.message}"
                        )
                    }
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

    fun toggleSoundPreview(context: Context) {
        if (_uiState.value.isSoundPreviewPlaying) {
            stopSoundPreview()
        } else {
            playSoundPreview(context)
        }
    }

    private fun playSoundPreview(context: Context) {
        stopSoundPreview()

        try {
            val soundId = _uiState.value.bomberSoundId
            mediaPlayer = MediaPlayer.create(context, soundId)?.apply {
                setOnCompletionListener {
                    _uiState.update { it.copy(isSoundPreviewPlaying = false) }
                    stopSoundPreview()
                }
                start()
            }
            _uiState.update { it.copy(isSoundPreviewPlaying = true) }
        } catch (e: Exception) {
            e.printStackTrace()
            stopSoundPreview()
        }
    }

    private fun stopSoundPreview() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
        }
        mediaPlayer = null
        _uiState.update { it.copy(isSoundPreviewPlaying = false) }
    }

    fun sendTelemetry() {
        if (!_uiState.value.telemetryEnabled) {
            _uiState.update { it.copy(logMessage = "Telemetry is disabled") }
            return
        }

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

    override fun onCleared() {
        stopSoundPreview()
    }
}