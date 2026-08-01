package cc.rccstudios.map.data.network.model

import com.google.gson.annotations.SerializedName

data class GithubReleaseDto(
    @SerializedName("tag_name")
    val tagName: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("body")
    val body: String?,
    @SerializedName("prerelease")
    val prerelease: Boolean,
    @SerializedName("assets")
    val assets: List<GithubAssetDto> = emptyList()
)

data class GithubAssetDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("browser_download_url")
    val downloadUrl: String
)