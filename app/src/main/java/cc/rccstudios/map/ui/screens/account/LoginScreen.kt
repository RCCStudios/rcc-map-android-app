package cc.rccstudios.map.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.rccstudios.map.ui.MainViewModel
import cc.rccstudios.map.R
import cc.rccstudios.map.ui.UiState

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    state: UiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.log_in_title),
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
            AuthTextField(
                text = stringResource(R.string.otp),
                placeholder = stringResource(R.string.otp_placeholder),
                value = state.otp ?: "",
                onValueChange = {
                    viewModel.onOtpChange(it)
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            AuthTextField(
                text = stringResource(R.string.server_url),
                placeholder = stringResource(R.string.server_url_placeholder),
                value = state.serverUrl,
                onValueChange = {
                    viewModel.onUrlChange(it)
                }
            )
        }

        AuthTextButton(
            prefixText = stringResource(R.string.dont_have_account),
            text = stringResource(R.string.register_button),
            onClick = { viewModel.toggleAuthMode() }
        )

        Spacer(modifier = Modifier.size(12.dp))

        val isButtonEnabled = !state.otp.isNullOrBlank() &&
                state.serverUrl.isNotBlank() &&
                !state.isLoading

        AuthButton(
            text = stringResource(R.string.log_in_button),
            onClick = { viewModel.login() },
            enabled = isButtonEnabled,
            isLoading = state.isLoading
        )

    }
}