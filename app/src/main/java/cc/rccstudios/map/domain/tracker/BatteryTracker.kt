package cc.rccstudios.map.domain.tracker

interface BatteryTracker {
    fun getBatteryStatus(): Int?
}