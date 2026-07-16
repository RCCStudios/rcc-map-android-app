package cc.rccstudios.map.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import cc.rccstudios.map.R
import cc.rccstudios.map.domain.usecase.CollectAndSendTelemetryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TelemetryService : Service(), KoinComponent {
    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase by inject()

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var trackingJob: Job? = null

    companion object {
        private const val CHANNEL_ID = "telemetry_channel"
        private const val NOTIFICATION_ID = 101

        private const val TRACKING_INTERVAL_MS = 60000L
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        startTracking()
        return START_STICKY
    }

    private fun startTracking() {
        trackingJob?.cancel()
        trackingJob = serviceScope.launch {
            while (isActive) {
                try {
                    collectAndSendTelemetryUseCase()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(TRACKING_INTERVAL_MS)
            }
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RCC Map Active")
            .setContentText("Telemetry collecting is active in real time")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "RCC Map Telemetry",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification about RCC Map background service"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        trackingJob?.cancel()
    }

    override fun onBind(p0: Intent?): IBinder? = null
}