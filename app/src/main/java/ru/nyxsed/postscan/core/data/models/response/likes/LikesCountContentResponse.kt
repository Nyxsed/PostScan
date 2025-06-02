package ru.nyxsed.postscan.core.data.models.response.likes

import com.google.gson.annotations.SerializedName

data class LikesCountContentResponse(
    @SerializedName("likes") val count : Int,
)