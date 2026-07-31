package cc.rccstudios.map.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    mapUrl: String
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (mapUrl.isBlank()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
        val finalUrl = remember(mapUrl, state.otp) {
            if (mapUrl.isBlank()) return@remember ""

            val baseUrl = mapUrl.toNormalizedUrl()
            if (state.otp.isBlank()) {
                baseUrl
            } else {
                baseUrl.toUri()
                    .buildUpon()
                    .appendQueryParameter("otp", state.otp)
                    .build()
                    .toString()
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
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