package ru.nyxsed.postscan.common.data.models.response.newsfeedget


import com.google.gson.annotations.SerializedName

data class SizeResponse(
    @SerializedName("height")
    val height: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int,
)