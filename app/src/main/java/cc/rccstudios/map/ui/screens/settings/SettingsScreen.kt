package cc.rccstudios.map.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import cc.rccstudios.map.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.rccstudios.map.domain.model.TimePeriod
import cc.rccstudios.map.ui.MainViewModel
import cc.rccstudios.map.ui.dialogs.timeperiod.TimePeriodDialog
import cc.rccstudios.map.utils.toNormalizedUrl
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    val formattedServerUrl = state.serverUrl.toNormalizedUrl()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPeriod by remember { mutableStateOf<TimePeriod?>(null) }

    Column(
        modifier = modifier
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.size(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingButton(
                isEnabled = state.telemetryEnabled,
                onToggle = {
                    if (it) {
                        haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                    } else {
                        haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                    }
                    viewModel.onTelemetryEnabledChange(it)
                },
                enabledIcon = Icons.Default.PowerSettingsNew,
                disabledIcon = Icons.Default.HighlightOff
            )

            Text(
                text = if (state.telemetryEnabled) {
                    stringResource(R.string.telemetry_active)
                } else {
                    stringResource(R.string.telemetry_disabled)
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }

        SettingSwitch(
            text = stringResource(R.string.battery_tracking),
            checked = state.batteryTrackingEnabled,
            onCheckedChange = {
                if (it) {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                }
                viewModel.onBatteryTrackingChange(it)
            },
            enabled = state.telemetryEnabled
        )

        SettingSwitch(
            text = stringResource(R.string.location_tracking),
            checked = state.locationTrackingEnabled,
            onCheckedChange = {
                if (it) {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                }
                viewModel.onLocationTrackingChange(it)
            },
            enabled = state.telemetryEnabled
        )

        SettingSwitch(
            text = stringResource(R.string.network_tracking),
            checked = state.networkTrackingEnabled,
            onCheckedChange = {
                if (it) {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                }
                viewModel.onNetworkTrackingChange(it)
            },
            description = stringResource(R.string.network_tracking_desc),
            enabled = state.telemetryEnabled
        )

        SettingSwitch(
            text = stringResource(R.string.screen_lock_tracking),
            checked = state.screenLockTrackingEnabled,
            onCheckedChange = {
                if (it) {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                }
                viewModel.onScreenLockTrackingChange(it)
            },
            enabled = state.telemetryEnabled
        )

        SettingSlider(
            text = stringResource(R.string.telemetry_interval),
            valueMs = state.telemetryInterval,
            onValueChange = {
                haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
            },
            onValueChangeFinished = {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                viewModel.onTelemetryIntervalChange(it)
            },
            enabled = state.telemetryEnabled
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        SettingSwitch(
            text = stringResource(R.string.bomber_switch),
            description = stringResource(R.string.bomber_switch_desc),
            checked = state.bomberEnabled,
            onCheckedChange = {
                if (it) {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                }
                viewModel.onBomberEnabledChange(it)
            }
        )

        SettingTimeSelector(
            text = stringResource(R.string.bomber_periods_title),
            addPeriod = { showAddDialog = true },
            updatePeriod = { period -> editingPeriod = period },
            removePeriod = { id -> viewModel.removeBomberSilencePeriod(id) },
            periods = state.bomberSilencePeriods
        )

        if (showAddDialog) {
            TimePeriodDialog(
                title = stringResource(R.string.bomber_periods_title),
                startText = stringResource(R.string.bomber_periods_add_start),
                endText = stringResource(R.string.bomber_periods_add_end),
                onDismissRequest = { showAddDialog = false },
                onConfirm = { start, end ->
                    viewModel.addBomberSilencePeriod(
                        TimePeriod(
                            startMinuteOfDay = start,
                            endMinuteOfDay = end
                        )
                    )
                }
            )
        }

        editingPeriod?.let { period ->
            TimePeriodDialog(
                title = stringResource(R.string.bomber_periods_title),
                startText = stringResource(R.string.bomber_periods_add_start),
                endText = stringResource(R.string.bomber_periods_add_end),
                onDismissRequest = { editingPeriod = null },
                initialStartMinute = period.startMinuteOfDay,
                initialEndMinute = period.endMinuteOfDay,
                onConfirm = { start, end ->
                    viewModel.updateBomberSilencePeriod(
                        period.copy(
                            startMinuteOfDay = start,
                            endMinuteOfDay = end
                        )
                    )
                }
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        SettingTextField(
            text = stringResource(R.string.server_url),
            placeholder = stringResource(R.string.server_url_placeholder),
            value = state.serverUrl,
            onValueChange = { newValue ->
                viewModel.onUrlChange(newValue)
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Text(
            text = stringResource(R.string.logcat),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .padding(
                    horizontal = 32.dp,
                    vertical = 12.dp
                )
        )

        Text(
            text = state.logMessage.ifBlank { stringResource(R.string.empty) },
            color = if (state.logMessage.isBlank()) {
                MaterialTheme.colorScheme.secondary
            } else if (state.logMessage.contains("Error", ignoreCase = true)) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .padding(
                    horizontal = 32.dp,
                    vertical = 12.dp
                )
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        val isSendTelemetryButtonEnabled = !state.token.isNullOrBlank() &&
                state.serverUrl.isNotBlank() &&
                state.telemetryEnabled &&
                !state.isLoading

        SettingButton(
            text = stringResource(R.string.send_telemetry_button),
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                viewModel.sendTelemetry()
            },
            enabled = isSendTelemetryButtonEnabled,
            isLoading = state.isLoading
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        SettingButton(
            text = stringResource(R.string.tos_link),
            description = stringResource(R.string.tos_link_desc),
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                uriHandler.openUri("${formattedServerUrl}/terms-of-service")
            },
            enabled = state.serverUrl.isNotEmpty(),
            icon = Icons.Default.Approval,
        )

        SettingButton(
            text = stringResource(R.string.github_link),
            description = stringResource(R.string.github_link_desc),
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                uriHandler.openUri("https://github.com/RCCStudios/")
            },
            icon = SimpleIcons.Github,
        )

        SettingButton(
            text = stringResource(R.string.update_button),
            description = stringResource(R.string.update_button_desc),
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                viewModel.checkUpdates()
            },
            icon = Icons.Default.Update
        )

        Spacer(modifier = Modifier.size(8.dp))
    }
}