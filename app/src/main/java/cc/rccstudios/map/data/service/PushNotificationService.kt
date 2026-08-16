package cc.rccstudios.map.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import cc.rccstudios.map.R
import cc.rccstudios.map.domain.usecase.CollectAndSendTelemetryUseCase
import cc.rccstudios.map.domain.usecase.UpdateFidUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PushNotificationService : FirebaseMessagingService(), KoinComponent {

    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase by inject()
    private val updateFidUseCase: UpdateFidUseCase by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        const val ACTION_FORCE_TELEMETRY = "FORCE_TELEMETRY"
        const val ACTION_BOMBER = "ACTION_BOMBER"
        const val BOMBER_CHANNEL_ID = "bomber_channel"
    }

    override fun onRegistered(token: String) {
        super.onRegistered(token)
        serviceScope.launch {
            updateFidUseCase(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val action = data["action"]

        when (action) {
            ACTION_FORCE_TELEMETRY -> handleForceTelemetry()
            ACTION_BOMBER -> handleBomberNotification(data)
            else -> {
                message.notification?.let {
                    showNotification(it.title ?: "Alert", it.body ?: "")
                }
            }
        }
    }

    private fun handleForceTelemetry() {
        serviceScope.launch {
            collectAndSendTelemetryUseCase()
        }
    }

    private fun handleBomberNotification(data: Map<String, String>) {
        TODO("Not implemented yet")
    }

    private fun showNotification(title: String, body: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BOMBER_CHANNEL_ID,
                getString(R.string.bomber_notification),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.bomber_notification)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, BOMBER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}