package cc.rccstudios.map.utils

import cc.rccstudios.map.domain.model.TimePeriod
import java.time.LocalTime

fun String.toNormalizedUrl(): String {
    if (isBlank()) return ""
    val url = if (startsWith("http://") || startsWith("https://")) this else "https://$this"
    return url.removeSuffix("/")
}

fun compareVersions(v1: String, v2: String): Int {
    val clean1 = v1.removePrefix("v").trim()
    val clean2 = v2.removePrefix("v").trim()

    val parts1 = clean1.split("-", limit = 2)
    val parts2 = clean2.split("-", limit = 2)

    val numbers1 = parts1[0].split(".").mapNotNull { it.toIntOrNull() }
    val numbers2 = parts2[0].split(".").mapNotNull { it.toIntOrNull() }

    val maxLength = maxOf(numbers1.size, numbers2.size)

    for (i in 0 until maxLength) {
        val num1 = numbers1.getOrElse(i) { 0 }
        val num2 = numbers2.getOrElse(i) { 0 }

        if (num1 != num2) {
            return num1.compareTo(num2)
        }
    }

    val hasSuffix1 = parts1.size > 1
    val hasSuffix2 = parts2.size > 1

    return when {
        !hasSuffix1 && hasSuffix2 -> 1
        hasSuffix1 && !hasSuffix2 -> -1
        hasSuffix1 && hasSuffix2 -> parts1[1].compareTo(parts2[1])
        else -> 0
    }
}

fun List<TimePeriod>.isSilenceNow(now: LocalTime = LocalTime.now()): Boolean {
    val nowMinutes = now.hour * 60 + now.minute
    return any { period ->
        if (!period.enabled) return@any false
        if (period.startMinuteOfDay <= period.endMinuteOfDay) {
            nowMinutes in period.startMinuteOfDay..period.endMinuteOfDay
        } else {
            nowMinutes >= period.startMinuteOfDay || nowMinutes <= period.endMinuteOfDay
        }
    }
}

fun Int.toNormalizedTime(): String {
    val h = this / 60
    val m = this % 60
    return "%02d:%02d".format(h, m)

}