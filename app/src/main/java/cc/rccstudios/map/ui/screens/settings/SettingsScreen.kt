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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.rccstudios.map.ui.MainViewModel
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

    val formattedServerUrl = state.serverUrl.toNormalizedUrl()

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
                isEnabled = state.isTelemetryEnabled,
                onToggle = { viewModel.onTelemetryEnabledChange(it) },
                enabledIcon = Icons.Default.PowerSettingsNew,
                disabledIcon = Icons.Default.HighlightOff
            )

            Text(
                text = if (state.isTelemetryEnabled) {
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
            checked = state.isBatteryTrackingEnabled,
            onCheckedChange = { viewModel.onBatteryTrackingChange(it) },
            enabled = state.isTelemetryEnabled
        )

        SettingSwitch(
            text = stringResource(R.string.location_tracking),
            checked = state.isLocationTrackingEnabled,
            onCheckedChange = { viewModel.onLocationTrackingChange(it) },
            enabled = state.isTelemetryEnabled
        )

        SettingSwitch(
            text = stringResource(R.string.network_tracking),
            checked = state.isNetworkTrackingEnabled,
            onCheckedChange = { viewModel.onNetworkTrackingChange(it) },
            description = stringResource(R.string.network_tracking_desc),
            enabled = state.isTelemetryEnabled
        )

        SettingSwitch(
            text = stringResource(R.string.screen_lock_tracking),
            checked = state.isScreenLockTrackingEnabled,
            onCheckedChange = { viewModel.onScreenLockTrackingChange(it) },
            enabled = state.isTelemetryEnabled
        )

        SettingSlider(
            text = stringResource(R.string.telemetry_interval),
            valueMs = state.telemetryInterval,
            onValueChange = { viewModel.onTelemetryIntervalChange(it) },
            enabled = state.isTelemetryEnabled
        )

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
            text = state.logMessage.ifBlank { stringResource(R.string.logcat_empty) },
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
                state.isTelemetryEnabled &&
                !state.isLoading

        SettingButton(
            text = stringResource(R.string.send_telemetry_button),
            onClick = {
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
            onClick = { uriHandler.openUri("${formattedServerUrl}/terms-of-service") },
            enabled = state.serverUrl.isNotEmpty(),
            icon = Icons.Default.Approval,
        )

        SettingButton(
            text = stringResource(R.string.github_link),
            description = stringResource(R.string.github_link_desc),
            onClick = { uriHandler.openUri("https://github.com/RCCStudios/") },
            icon = SimpleIcons.Github,
        )

        SettingButton(
            text = stringResource(R.string.update_button),
            description = stringResource(R.string.update_button_desc),
            onClick = { viewModel.checkUpdates() },
            icon = Icons.Default.Update
        )

        Spacer(modifier = Modifier.size(8.dp))
    }
}