package cc.rccstudios.map.data.tracker.screenlock

import android.app.KeyguardManager
import android.content.Context

class ScreenLockTrackerImpl(
    private val context: Context
) : cc.rccstudios.map.domain.tracker.ScreenLockTracker {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    private var lockTimestamp: Long? = null
    override fun getScreenLockStatus(): Long {
        val isLocked = keyguardManager.isDeviceLocked
        return if (isLocked) {
            if (lockTimestamp == null) {
                lockTimestamp = System.currentTimeMillis()
            }
            lockTimestamp ?: System.currentTimeMillis()
        } else {
            lockTimestamp = null
            0L
        }
    }
}