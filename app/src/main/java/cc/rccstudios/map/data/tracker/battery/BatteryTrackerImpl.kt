package cc.rccstudios.map.data.tracker.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryTrackerImpl(
    private val context: Context
) : cc.rccstudios.map.domain.tracker.BatteryTracker {
    override fun getBatteryStatus(): Int? {
        val batteryStatus: Intent? =
            IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                context.registerReceiver(null, ifilter)
            }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        if (scale < 0 || level < 0) {
            return null
        }
        return (level * 100 / scale.toFloat()).toInt()
    }
}