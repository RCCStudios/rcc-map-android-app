package cc.rccstudios.map.data.tracker.screenlock

import android.app.KeyguardManager
import android.content.Context

class ScreenLockTrackerImpl(
    private val context: Context
) : cc.rccstudios.map.domain.tracker.ScreenLockTracker {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    override fun getScreenLockStatus(): Boolean {
        val isLocked = keyguardManager.isDeviceLocked
        return isLocked
    }
}