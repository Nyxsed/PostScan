package ru.nyxsed.postscan.core.data.models.response.newsfeedget


import com.google.gson.annotations.SerializedName

data class AlbumResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("thumb")
    val thumb: ThumbResponse,
)