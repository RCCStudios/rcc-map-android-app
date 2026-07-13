package cc.rccstudios.map.data.local.screenlockstatus

import android.app.KeyguardManager
import android.content.Context

class ScreenLockStatusTracker(
    private val context: Context
) {
    fun getScreenLockStatus(): Boolean {
        val myKM = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return myKM.isDeviceLocked
    }
}