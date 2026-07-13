package cc.rccstudios.map.domain.tracker

interface LocationTracker {
    suspend fun getLocationStatus(): Pair<Double, Double>?
}