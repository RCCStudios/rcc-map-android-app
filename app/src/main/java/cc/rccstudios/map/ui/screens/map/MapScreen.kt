package cc.rccstudios.map.ui.screens.map

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.rccstudios.map.R
import cc.rccstudios.map.ui.MainViewModel
import cc.rccstudios.map.utils.toNormalizedUrl

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapScreen(
    viewModel: MainViewModel,
    mapUrl: String,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (mapUrl.isBlank()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = stringResource(R.string.map_placeholder),
                modifier = Modifier.size(96.dp)
            )
            Text(
                text = stringResource(R.string.map_placeholder),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
    } else {
        val isDarkTheme = isSystemInDarkTheme()
        val finalUrl = remember(mapUrl, state.token, isDarkTheme) {
            if (mapUrl.isBlank()) return@remember ""

            val uriBuilder = mapUrl.toNormalizedUrl()
                .toUri()
                .buildUpon()
                .appendQueryParameter("isDarkTheme", if (isDarkTheme) "true" else "false")
            if (!state.token.isNullOrBlank()) {
                uriBuilder.appendQueryParameter("token", state.token)
            }

            uriBuilder.build().toString()
        }

        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true

                    webViewClient = WebViewClient()

                    loadUrl(finalUrl)
                }
            },
            update = { webView ->
                if (webView.url != finalUrl) {
                    webView.loadUrl(finalUrl)
                }
            }
        )
    }
}