package cc.rccstudios.map.data.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import cc.rccstudios.map.BomberActivity
import cc.rccstudios.map.R
import cc.rccstudios.map.data.service.TelemetryService.Companion.ACTION_STOP_NOTIFICATION

class BomberService : Service() {
    companion object {
        private const val TAG = "BomberService"
        const val NOTIFICATION_ID = 9001

        const val ACTION_START = "BOMBER_START"
        const val ACTION_STOP = "BOMBER_STOP"

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_SENDER = "extra_sender"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var torchCameraId: String? = null
    private var torchOn = false
    private var effectsRunning = false


    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

    private val cameraManager: CameraManager by lazy {
        getSystemService(CAMERA_SERVICE) as CameraManager
    }

    private val notificationManager: NotificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopEffects()
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                val title = intent?.getStringExtra(EXTRA_TITLE)
                val body = intent?.getStringExtra(EXTRA_BODY)
                val sender = intent?.getStringExtra(EXTRA_SENDER)

                if (!effectsRunning) {
                    startEffects(title, body, sender)
                    effectsRunning = true
                } else {
                    notificationManager.notify(NOTIFICATION_ID, buildBomberNotification(title, body, sender))
                    launchBomberActivity(title, body, sender)
                }

            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startEffects(title: String?, body: String?, sender: String?) {
        startForeground(NOTIFICATION_ID, buildBomberNotification(title, body, sender))
        startVibration()
        startSound()
        startTorch()
    }

    private fun launchBomberActivity(title: String?, body: String?, sender: String?) {
        val intent = Intent(this, BomberActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_BODY, body)
            putExtra(EXTRA_SENDER, sender)
        }
        startActivity(intent)
    }

    private fun stopEffects() {
        vibrator.cancel()
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        setTorch(false)
        effectsRunning = false
    }

    private fun startVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 400, 100)
            val effect = VibrationEffect.createWaveform(pattern, 0)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(1200L)
        }
    }

    private fun startSound() {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                val afd = resources.openRawResourceFd(R.raw.bomber_alarm_1)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start sound", e)
        }
    }

    private fun startTorch() {
        try {
            val id = torchCameraId ?: findFlashCameraId() ?: return
            torchCameraId = id
            setTorch(true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start torch", e)
        }
    }

    private fun findFlashCameraId(): String? {
        return cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }

    private fun setTorch(on: Boolean) {
        val id = torchCameraId ?: return
        try {
            cameraManager.setTorchMode(id, on)
            torchOn = on
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle torch", e)
        }
    }

    private fun buildBomberNotification(title: String?, body: String?, sender: String?): Notification {
        val fullScreenIntent = Intent(this, BomberActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_BODY, body)
            putExtra(EXTRA_SENDER, sender)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, BomberService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canUseFullScreen = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            notificationManager.canUseFullScreenIntent()
        } else true

        val builder = NotificationCompat.Builder(
            this,
            PushPayloadHandler.BOMBER_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title ?: getString(R.string.bomber))
            .setContentText(body ?: getString(R.string.bomber_desc))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_stop,
                getString(R.string.stop_button),
                stopPendingIntent
            )

        body?.let { builder.setContentText(it) }

        if (canUseFullScreen) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
        } else {
            builder.setContentIntent(fullScreenPendingIntent)
        }

        return builder.build()
    }

    override fun onDestroy() {
        stopEffects()
        super.onDestroy()
    }
}