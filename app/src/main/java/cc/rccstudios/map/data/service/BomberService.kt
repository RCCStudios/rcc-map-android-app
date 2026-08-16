package cc.rccstudios.map.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
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
import cc.rccstudios.map.R

class BomberService : Service() {
    companion object {
        private const val TAG = "BomberService"
        const val FGS_NOTIFICATION_ID = 9001
        const val FGS_CHANNEL_ID = "bomber_fgs_channel"

        const val ACTION_START = "cc.rccstudios.map.action.BOMBER_START"
        const val ACTION_STOP = "cc.rccstudios.map.action.BOMBER_STOP"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var torchCameraId: String? = null
    private var torchOn = false

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

    override fun onCreate() {
        super.onCreate()
        createFgsChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopEffects()
                stopSelf()
                return START_NOT_STICKY
            }
            else -> startEffects()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startEffects() {
        startForeground(FGS_NOTIFICATION_ID, buildFgsNotification())
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
    }

    private fun startVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 800, 400)
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

    private fun buildFgsNotification(): Notification {
        return NotificationCompat.Builder(this, FGS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.bomber_notification))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .build()
    }

    private fun createFgsChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            FGS_CHANNEL_ID,
            "Bomber service",
            NotificationManager.IMPORTANCE_MIN
        )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    override fun onDestroy() {
        stopEffects()
        super.onDestroy()
    }
}