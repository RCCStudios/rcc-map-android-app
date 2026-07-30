package cc.rccstudios.map.utils

fun String.toNormalizedUrl(): String {
    if (isBlank()) return ""
    val url = if (startsWith("http://") || startsWith("https://")) this else "https://$this"
    return url.removeSuffix("/")
}