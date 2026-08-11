package cc.rccstudios.map.ui.screens.map.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri

open class MapWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    init {
        webViewClient = MapWebViewClient()
    }

    private inner class MapWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            val url = request.url.toString()

            if (url.startsWith("tg://") || isTelegramWebUrl(request.url)) {
                return try {
                    val intent = Intent(Intent.ACTION_VIEW, request.url)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    true
                } catch (e: ActivityNotFoundException) {
                    val webIntent = Intent(Intent.ACTION_VIEW, "https://t.me/".toUri())
                    webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(webIntent)
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            return false
        }

        private fun isTelegramWebUrl(uri: Uri): Boolean {
            val host = uri.host ?: return false
            return host.endsWith("t.me") || host.endsWith("telegram.me")
        }
    }
}