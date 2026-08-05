package cc.rccstudios.map.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import cc.rccstudios.map.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.rccstudios.map.ui.MainViewModel
import cc.rccstudios.map.utils.toNormalizedUrl
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github
import kotlin.math.roundToLong

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
    ) {
        Spacer(modifier = Modifier.size(8.dp))

        SettingSwitch(
            text = stringResource(R.string.battery_tracking),
            checked = state.isBatteryTrackingEnabled,
            onCheckedChange = { viewModel.onBatteryTrackingChange(it) }
        )

        SettingSwitch(
            text = stringResource(R.string.location_tracking),
            checked = state.isLocationTrackingEnabled,
            onCheckedChange = { viewModel.onLocationTrackingChange(it) }
        )

        SettingSwitch(
            text = stringResource(R.string.network_tracking),
            checked = state.isNetworkTrackingEnabled,
            onCheckedChange = { viewModel.onNetworkTrackingChange(it) },
            description = stringResource(R.string.network_tracking_desc)
        )

        SettingSwitch(
            text = stringResource(R.string.screen_lock_tracking),
            checked = state.isScreenLockTrackingEnabled,
            onCheckedChange = { viewModel.onScreenLockTrackingChange(it) }
        )

        SettingSlider(
            text = stringResource(R.string.telemetry_interval),
            valueMs = state.telemetryInterval,
            onValueChange = { viewModel.onTelemetryIntervalChange(it) }
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

@Composable
fun SettingSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Row(
        modifier = modifier
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleLarge
            )
            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingSlider(
    text: String,
    valueMs: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderValue by remember(valueMs) {
        mutableFloatStateOf((valueMs / 1000f).coerceIn(5f, 300f))
    }

    Column(
        modifier = modifier
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
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleLarge
            )

            val seconds = sliderValue.toInt()
            val timeText = if (seconds >= 60 && seconds % 60 == 0) {
                "${seconds / 60} ${stringResource(R.string.minutes)}"
            } else {
                "$seconds ${stringResource(R.string.seconds)}"
            }

            Text(
                text = timeText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                sliderValue = newValue
            },
            onValueChangeFinished = {
                val updatedIntervalMs = sliderValue.roundToLong() * 1000L
                onValueChange(updatedIntervalMs)
            },
            valueRange = 5f..300f
        )
    }
}

@Composable
fun SettingTextField(
    text: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}

@Composable
fun SettingButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

//@Composable
//fun SettingExternalLink(
//    text: String,
//    link: String,
//    icon: ImageVector,
//    uriHandler: UriHandler,
//    description: String?,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        onClick = { uriHandler.openUri(link) },
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant
//        ),
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween,
//        ) {
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(end = 16.dp)
//            ) {
//                Text(
//                    text = text,
//                    textAlign = TextAlign.Left,
//                    style = MaterialTheme.typography.titleLarge
//                )
//                if (!description.isNullOrBlank()) {
//                    Text(
//                        text = description,
//                        textAlign = TextAlign.Left,
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                }
//            }
//
//            Icon(
//                imageVector = icon,
//                contentDescription = text,
//                modifier = Modifier.size(40.dp)
//            )
//        }
//    }
//}

@Composable
fun SettingButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector,
    enabled: Boolean? = null,
    description: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        enabled = enabled ?: true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = text,
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleLarge
                )
                if (!description.isNullOrBlank()) {
                    Text(
                        text = description,
                        textAlign = TextAlign.Left,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}