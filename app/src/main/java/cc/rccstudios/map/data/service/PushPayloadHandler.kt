package cc.rccstudios.map.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import cc.rccstudios.map.R
import cc.rccstudios.map.domain.model.PushPayload
import cc.rccstudios.map.domain.usecase.CollectAndSendTelemetryUseCase
import cc.rccstudios.map.domain.usecase.ShouldSuppressBomberUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PushPayloadHandler(
    private val context: Context,
    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase,
    private val shouldSuppressBomberUseCase: ShouldSuppressBomberUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        const val PUSH_PAYLOAD_HANDLER_TAG = "PushPayloadHandler"
        const val ACTION_FORCE_TELEMETRY = "FORCE_TELEMETRY"
        const val ACTION_BOMBER = "BOMBER"
        const val PUSH_CHANNEL_ID = "push_channel"
        const val BOMBER_CHANNEL_ID = "bomber_channel"

        private data class ChannelSpec(
            val id: String,
            val nameRes: Int,
            val descRes: Int,
            val importance: Int
        )

        private val CHANNELS = listOf(
            ChannelSpec(
                PUSH_CHANNEL_ID,
                R.string.push_notification,
                R.string.push_notification,
                NotificationManager.IMPORTANCE_DEFAULT
            ),
            ChannelSpec(
                BOMBER_CHANNEL_ID,
                R.string.bomber_notification,
                R.string.bomber_notification,
                NotificationManager.IMPORTANCE_HIGH
            ),
        )
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        CHANNELS.forEach { spec ->
            val channel = NotificationChannel(
                spec.id,
                context.getString(spec.nameRes),
                spec.importance
            ).apply {
                description = context.getString(spec.descRes)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun handle(payload: PushPayload) {
        when (payload.action) {
            ACTION_FORCE_TELEMETRY -> handleForceTelemetry()
            ACTION_BOMBER -> handleBomberNotification(payload.data)
            else -> showNotification(payload.title ?: "Alert", payload.body ?: "")
        }
    }

    private fun handleForceTelemetry() {
        scope.launch { collectAndSendTelemetryUseCase(skipCheck = true) }
    }

    private fun handleBomberNotification(data: Map<String, String>) {
        scope.launch {
            if (shouldSuppressBomberUseCase()) {
                Log.d(PUSH_PAYLOAD_HANDLER_TAG, "Bomber suppressed: within silence period")
                return@launch
            }
            val intent = Intent(context, BomberService::class.java).apply {
                action = BomberService.ACTION_START
                putExtra(BomberService.EXTRA_TITLE, data["title"])
                putExtra(BomberService.EXTRA_BODY, data["body"])
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    private fun showNotification(title: String, body: String, channelId: String = PUSH_CHANNEL_ID) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}