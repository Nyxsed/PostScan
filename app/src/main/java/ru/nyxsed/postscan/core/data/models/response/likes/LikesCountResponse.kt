package ru.nyxsed.postscan.core.data.models.response.likes

import com.google.gson.annotations.SerializedName
import ru.nyxsed.postscan.core.data.models.response.ErrorResponse

data class LikesCountResponse(
    @SerializedName("response")
    val likes: LikesCountContentResponse,
    @SerializedName("error")
    val error: ErrorResponse?,
)
