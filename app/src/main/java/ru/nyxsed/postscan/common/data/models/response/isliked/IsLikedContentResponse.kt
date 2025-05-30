package ru.nyxsed.postscan.common.data.models.response.isliked


import com.google.gson.annotations.SerializedName

data class IsLikedContentResponse(
    @SerializedName("liked")
    val liked: Int,
)