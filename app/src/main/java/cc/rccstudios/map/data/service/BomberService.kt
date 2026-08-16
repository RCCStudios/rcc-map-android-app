package cc.rccstudios.map.data.service

import android.app.Notification
import android.app.NotificationChannel
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

class BomberService : Service() {
    companion object {
        private const val TAG = "BomberService"
        const val NOTIFICATION_ID = 9001

        const val ACTION_START = "cc.rccstudios.map.action.BOMBER_START"
        const val ACTION_STOP = "cc.rccstudios.map.action.BOMBER_STOP"

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
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
        getSystemService(Context.CAMERA_SERVICE) as CameraManager
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
                if (!effectsRunning) {
                    startEffects(
                        title = intent?.getStringExtra(EXTRA_TITLE),
                        body = intent?.getStringExtra(EXTRA_BODY)
                    )
                    effectsRunning = true
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startEffects(title: String?, body: String?) {
        startForeground(NOTIFICATION_ID, buildBomberNotification(title, body))
        startVibration()
        startSound()
        startTorch()
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

    private fun buildBomberNotification(title: String?, body: String?): Notification {
        val fullScreenIntent = Intent(this, BomberActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canUseFullScreen = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            notificationManager.canUseFullScreenIntent()
        } else true

        val builder = NotificationCompat.Builder(this, PushNotificationService.BOMBER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title ?: getString(R.string.bomber_notification))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)

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