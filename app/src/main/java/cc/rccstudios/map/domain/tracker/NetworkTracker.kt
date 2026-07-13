package cc.rccstudios.map.domain.tracker

interface NetworkTracker {
    fun getNetworkStatus(): Int?
}