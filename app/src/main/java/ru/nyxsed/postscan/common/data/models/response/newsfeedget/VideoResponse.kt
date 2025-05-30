package ru.nyxsed.postscan.common.data.models.response.newsfeedget


import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("image")
    val image: List<ImageResponse>,
)