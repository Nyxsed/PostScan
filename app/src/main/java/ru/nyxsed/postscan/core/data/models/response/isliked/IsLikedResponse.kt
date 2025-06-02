package ru.nyxsed.postscan.core.data.models.response.isliked


import com.google.gson.annotations.SerializedName
import ru.nyxsed.postscan.core.data.models.response.ErrorResponse

data class IsLikedResponse(
    @SerializedName("response")
    val response: IsLikedContentResponse?,
    @SerializedName("error")
    val error: ErrorResponse?,
)