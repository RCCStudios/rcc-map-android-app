package cc.rccstudios.map.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import cc.rccstudios.map.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.rccstudios.map.ui.MainModelView
import cc.rccstudios.map.ui.theme.RCCMapTheme

@Composable
fun SettingsScreen(
    viewModel: MainModelView,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.size(8.dp))
        SettingSwitch(
            text = stringResource(R.string.battery_tracking),
            checked = state.isBatteryTrackingEnabled,
            onCheckedChange = { viewModel.onBatteryTrackingChanged(it) }
        )
        SettingSwitch(
            text = stringResource(R.string.location_tracking),
            checked = state.isLocationTrackingEnabled,
            onCheckedChange = { viewModel.onLocationTrackingChanged(it) }
        )
        SettingSwitch(
            text = stringResource(R.string.network_tracking),
            checked = state.isNetworkTrackingEnabled,
            onCheckedChange = { viewModel.onNetworkTrackingChanged(it) },
            description = stringResource(R.string.network_tracking_desc)
        )
        SettingSwitch(
            text = stringResource(R.string.screen_lock_tracking),
            checked = state.isScreenLockTrackingEnabled,
            onCheckedChange = { viewModel.onScreenLockTrackingChanged(it) }
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        SettingTextField(
            text = stringResource(R.string.backend_server_url),
            placeholder = stringResource(R.string.backend_server_url_placeholder),
            value = state.backendUrl,
            onValueChange = { newValue ->
                viewModel.onUrlChange(newValue)
            }
        )
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
            if (!description.isNullOrEmpty()) {
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