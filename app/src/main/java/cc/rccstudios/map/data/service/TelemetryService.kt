package cc.rccstudios.map.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import cc.rccstudios.map.MainActivity
import cc.rccstudios.map.R
import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.domain.usecase.CollectAndSendTelemetryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TelemetryService : Service(), KoinComponent {
    private val collectAndSendTelemetryUseCase: CollectAndSendTelemetryUseCase by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var telemetryJob: Job? = null

    private var currentInterval: Long = 60000L

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_UPDATE_INTERVAL = "ACTION_UPDATE_INTERVAL"
        const val EXTRA_INTERVAL = "EXTRA_INTERVAL"
        const val CHANNEL_ID = "telemetry_channel"
        const val NOTIFICATION_ID = 101
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            buildNotification(getString(R.string.telemetry_active_notification))
        )

        when (intent?.action) {
            ACTION_STOP -> {
                stopTelemetry()
                serviceScope.launch {
                    settingsRepository.saveTelemetryEnabled(false)
                }
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_UPDATE_INTERVAL -> {
                val newInterval = intent.getLongExtra(EXTRA_INTERVAL, 60000L)
                if (newInterval != currentInterval) {
                    currentInterval = newInterval
                    restartTelemetryLoop()
                }
            }
            else -> {
                val initialInterval = intent?.getLongExtra(EXTRA_INTERVAL, 60000L) ?: 60000L
                currentInterval = initialInterval
                startTelemetry()
            }
        }
        return START_STICKY
    }

    private fun startTelemetry() {
        telemetryJob?.cancel()
        telemetryJob = serviceScope.launch {
            while (isActive) {
                try {
                    val result = collectAndSendTelemetryUseCase()
                    result.onSuccess { telemetry ->
                        val shortText = getString(R.string.telemetry_active_notification)
                        val batteryText = telemetry.batteryStatus?.let { "\uD83D\uDD0B ${getString(R.string.battery)}: $it%" } ?: "N/A"
                        val locationText = if (telemetry.latitude != null && telemetry.longitude != null) {
                            "\uD83C\uDF0D ${getString(R.string.location)}: %.4f, %.4f".format(telemetry.latitude, telemetry.longitude)
                        } else {
                            "\uD83C\uDF0D ${getString(R.string.location)}: ${getString(R.string.no_location_data)}"
                        }
                        val networkText = "\uD83C\uDF10 ${getString(R.string.network)}: ${when (telemetry.networkStatus) {
                            0 -> getString(R.string.unknown)
                            1 -> getString(R.string.wifi)
                            2 -> getString(R.string.ethernet)
                            3 -> getString(R.string.cellular)
                            else -> getString(R.string.unknown)
                        }}"
                        val screenLockText = if (telemetry.screenLockStatus ?: true) {
                            "\uD83D\uDD12 ${getString(R.string.screen_lock)}: ${getString(R.string.locked)}"
                        } else {
                            "\uD83D\uDD13 ${getString(R.string.screen_lock)}: ${getString(R.string.unlocked)}"
                        }
                        val detailedText = listOfNotNull(
                            locationText,
                            batteryText,
                            networkText,
                            screenLockText
                        ).joinToString("\n")
                        updateNotification(shortText, detailedText)
                    }.onFailure { error ->
                        updateNotification(
                            "${getString(R.string.error)}: ${error.localizedMessage ?: getString(R.string.offline)}"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    updateNotification(getString(R.string.telemetry_error))
                }
                delay(currentInterval)
            }
        }
    }

    private fun restartTelemetryLoop() {
        telemetryJob?.cancel()
        startTelemetry()
    }

    private fun stopTelemetry() {
        telemetryJob?.cancel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        notificationManager.cancel(NOTIFICATION_ID)
        stopSelf()
    }

    private fun updateNotification(shortText: String, detailedText: String? = null) {
        val updatedNotification = buildNotification(shortText, detailedText)
        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
    }

    private fun buildNotification(
        shortText: String,
        detailedText: String? = null
    ): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = Intent(this, TelemetryService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(shortText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                R.drawable.ic_stop,
                getString(R.string.stop_button),
                stopPendingIntent
            )

        if (!detailedText.isNullOrBlank()) {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(detailedText)
            )
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "RCC Map Telemetry",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "RCC Map background service"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        telemetryJob?.cancel()
        notificationManager.cancel(NOTIFICATION_ID)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null
}