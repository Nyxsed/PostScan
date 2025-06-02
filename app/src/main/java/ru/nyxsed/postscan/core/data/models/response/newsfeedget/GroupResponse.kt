package ru.nyxsed.postscan.core.data.models.response.newsfeedget


import com.google.gson.annotations.SerializedName

data class GroupResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("photo_50")
    val photo50: String,
)