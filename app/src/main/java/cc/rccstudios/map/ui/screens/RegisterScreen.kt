package cc.rccstudios.map.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.rccstudios.map.ui.MainModelView
import cc.rccstudios.map.R

@Composable
fun RegisterScreen(
    viewModel: MainModelView,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.padding(top = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.register_title),
            textAlign = TextAlign.Center,
            fontSize = 28.sp
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 32.dp,
                    vertical = 16.dp
                )
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(
                    horizontal = 32.dp,
                    vertical = 24.dp
                )
        ) {
            RegisterTextField(
                text = stringResource(R.string.username),
                placeholder = stringResource(R.string.username_placeholder),
                value = state.registerName,
                onValueChange = {
                    viewModel.onNameChange(it)
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            RegisterTextField(
                text = stringResource(R.string.otp),
                placeholder = stringResource(R.string.otp_placeholder),
                value = state.registerKey,
                onValueChange = {
                    viewModel.onKeyChange(it)
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            RegisterTextField(
                text = stringResource(R.string.server_url),
                placeholder = stringResource(R.string.server_url_placeholder),
                value = state.serverUrl,
                onValueChange = {
                    viewModel.onUrlChange(it)
                }
            )
        }

        val isButtonEnabled = state.registerName.isNotBlank() &&
                state.registerKey.isNotBlank() &&
                state.serverUrl.isNotBlank() &&
                !state.isLoading

        Button(
            onClick = {
                viewModel.register()
            },
            enabled = isButtonEnabled,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.register_button),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        if (state.logMessage.isNotBlank()) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = state.logMessage,
                color = if (state.logMessage.contains("Error", ignoreCase = true)) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun RegisterTextField(
    text: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleLarge
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}