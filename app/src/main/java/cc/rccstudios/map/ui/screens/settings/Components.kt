package cc.rccstudios.map.ui.screens.settings

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.round
import kotlin.math.roundToLong

@Composable
fun SettingSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
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
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
fun SettingSlider(
    text: String,
    valueMs: Long,
    onValueChange: (Long) -> Unit,
    enabled: Boolean,
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
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
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
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp),
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                val step = 5f
                sliderValue = (round(newValue / step) * step).coerceIn(5f, 300f)
            },
            onValueChangeFinished = {
                val updatedIntervalMs = sliderValue.roundToLong() * 1000L
                onValueChange(updatedIntervalMs)
            },
            valueRange = 5f..300f,
            enabled = enabled
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

@Composable
fun SettingButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean? = null,
    description: String? = null
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

@Composable
fun SettingButton(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    enabledIcon: ImageVector,
    disabledIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isEnabled) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.errorContainer
        },
        label = "TelemetryButtonBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isEnabled) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onErrorContainer
        },
        label = "TelemetryButtonContent"
    )

    Surface(
        onClick = { onToggle(!isEnabled) },
        shape = CircleShape,
        color = backgroundColor,
        contentColor = contentColor,
        shadowElevation = 6.dp,
        modifier = modifier
            .size(160.dp)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = if (isEnabled) enabledIcon else disabledIcon,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp)
        )
    }
}