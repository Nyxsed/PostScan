package ru.nyxsed.postscan.common.data.models.response.newsfeedget


import com.google.gson.annotations.SerializedName

data class LikesResponse(
    @SerializedName("user_likes")
    val userLikes: Int,
)