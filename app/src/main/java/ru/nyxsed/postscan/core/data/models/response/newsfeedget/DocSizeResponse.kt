package ru.nyxsed.postscan.core.data.models.response.newsfeedget

import com.google.gson.annotations.SerializedName

data class DocSizeResponse(
    @SerializedName("height")
    val height: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("src")
    val src: String,
    @SerializedName("width")
    val width: Int,
)