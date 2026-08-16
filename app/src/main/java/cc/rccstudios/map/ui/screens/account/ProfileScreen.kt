package cc.rccstudios.map.ui.screens.account

import android.content.ClipData
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cc.rccstudios.map.ui.MainViewModel
import cc.rccstudios.map.R
import cc.rccstudios.map.ui.UiState
import cc.rccstudios.map.utils.toNormalizedUrl
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    scope: CoroutineScope,
    state: UiState,
    context: Context,
    uriHandler: UriHandler,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateUser(context, it) }
    }
    val clipboardManager = LocalClipboard.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shadowElevation = 4.dp,
                enabled = !state.isLoading,
                onClick = {
                    if (isEditing) {
                        viewModel.updateUser()
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = if (isEditing) {
                        Icons.Default.Save
                    } else {
                        Icons.Default.Edit
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                )
            }
        }

        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            if (state.avatarPath.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("${state.serverUrl.toNormalizedUrl()}${state.avatarPath}")
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .build(),
                    contentDescription = stringResource(R.string.avatar_desc),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(192.dp)
                        .clip(CircleShape)
                        .background(Color.White, CircleShape)
                        .border(
                            BorderStroke(
                                4.dp,
                                MaterialTheme.colorScheme.onSurfaceVariant
                            ), CircleShape
                        )
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(192.dp)
                        .clip(CircleShape)
                        .background(Color.White, CircleShape)
                        .border(
                            BorderStroke(
                                4.dp,
                                MaterialTheme.colorScheme.onSurfaceVariant
                            ), CircleShape
                        )
                ) {
                    Text(
                        text = state.username.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (isEditing) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shadowElevation = 4.dp,
                    enabled = !state.isLoading,
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        if (!isEditing) {
            Text(
                text = state.username.ifBlank { "User" },
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            if (state.telegram.isNotBlank()) {
                AuthTextButton(
                    text = "TG: @${state.telegram}",
                    onClick = {
                        uriHandler.openUri(
                            "tg://resolve?domain=${state.telegram.removePrefix("@")}"
                        )
                    }
                )
            } else {
                AuthTextButton(
                    text = "TG: ${stringResource(R.string.not_set)}",
                    onClick = {
                        Toast.makeText(
                            context,
                            R.string.not_set,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 32.dp,
                        vertical = 8.dp
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.otp ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.otp)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                enabled = !state.token.isNullOrBlank(),
                                onClick = {
                                    scope.launch {
                                        clipboardManager.setClipEntry(
                                            ClipEntry(
                                                ClipData.newPlainText("OTP", state.otp)
                                            )
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = stringResource(R.string.copy_button)
                                )
                            }
                        }
                    )

                    AuthButton(
                        icon = Icons.Default.Refresh,
                        description = stringResource(R.string.refresh_button),
                        onClick = { viewModel.getOtp() },
                        isLoading = state.isLoading,
                        enabled = !state.token.isNullOrBlank(),
                    )
                }

                OutlinedTextField(
                    value = state.token ?: "Empty",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.token)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            enabled = !state.token.isNullOrBlank(),
                            onClick = {
                                scope.launch {
                                    if (!state.token.isNullOrBlank()) {
                                        clipboardManager.setClipEntry(
                                            ClipEntry(
                                                ClipData.newPlainText("Token", state.token)
                                            )
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = stringResource(R.string.copy_button)
                            )
                        }
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 32.dp,
                        vertical = 8.dp
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    )
            ) {
                AuthTextField(
                    text = stringResource(R.string.username),
                    placeholder = stringResource(R.string.username_placeholder),
                    value = state.username,
                    onValueChange = {
                        viewModel.onUsernameChange(it)
                    }
                )

                AuthTextField(
                    text = stringResource(R.string.telegram),
                    placeholder = stringResource(R.string.telegram_placeholder),
                    value = state.telegram,
                    onValueChange = {
                        viewModel.onTelegramChange(it)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthButton(
                text = stringResource(R.string.log_out_button),
                onClick = {
                    isEditing = false
                    viewModel.logout()
                },
                enabled = !state.isLoading,
                isLoading = state.isLoading,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}