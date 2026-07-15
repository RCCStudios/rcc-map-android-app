package cc.rccstudios.map.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.rccstudios.map.ui.MainModelView
import cc.rccstudios.map.ui.theme.RCCMapTheme
import cc.rccstudios.map.R

@Composable
fun RegisterScreen(
//    viewModel: MainModelView,
    modifier: Modifier = Modifier
) {
//    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier,
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
                text = stringResource(R.string.register_name),
                placeholder = stringResource(R.string.register_name_placeholder),
                value = "",
                onValueChange = {}
            )
            Spacer(modifier = Modifier.size(8.dp))
            RegisterTextField(
                text = stringResource(R.string.register_key),
                placeholder = stringResource(R.string.register_key_placeholder),
                value = "",
                onValueChange = {}
            )
            Spacer(modifier = Modifier.size(8.dp))
            RegisterTextField(
                text = stringResource(R.string.backend_server_url),
                placeholder = stringResource(R.string.backend_server_url_placeholder),
                value = "",
                onValueChange = {}
            )
        }
        Button(
            onClick = {},
            content = {
                Text(
                    text = stringResource(R.string.register_button),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            },
        )
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
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    RCCMapTheme {
        RegisterScreen(modifier = Modifier.fillMaxSize())
    }
}