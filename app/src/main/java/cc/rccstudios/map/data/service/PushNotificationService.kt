package cc.rccstudios.map.data.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import cc.rccstudios.map.BomberActivity
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

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class PushNotificationService : FirebaseMessagingService(), KoinComponent {

    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase by inject()
    private val updateFidUseCase: UpdateFidUseCase by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private const val TAG = "PushNotificationService"

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

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        CHANNELS.forEach { spec ->
            val channel = NotificationChannel(
                spec.id,
                getString(spec.nameRes),
                spec.importance
            ).apply {
                description = getString(spec.descRes)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onRegistered(fid: String) {
        super.onRegistered(fid)
        Log.d(TAG, "Registered FID: $fid")
        serviceScope.launch {
            updateFidUseCase(fid)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        when (val action = message.data["action"]) {
            ACTION_FORCE_TELEMETRY -> handleForceTelemetry()
            ACTION_BOMBER -> handleBomberNotification(message.data)
            else -> {
                message.notification?.let {
                    showNotification(it.title ?: "Alert", it.body ?: "")
                }
            }
        }
    }

    private fun handleForceTelemetry() {
        serviceScope.launch {
            collectAndSendTelemetryUseCase(skipCheck = true)
        }
    }

    private fun handleBomberNotification(data: Map<String, String>) {
        val intent = Intent(this, BomberService::class.java).apply {
            action = BomberService.ACTION_START
            putExtra(BomberService.EXTRA_TITLE, data["title"])
            putExtra(BomberService.EXTRA_BODY, data["body"])
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        channelId: String = PUSH_CHANNEL_ID
    ) {
        val notification = NotificationCompat.Builder(this, channelId)
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