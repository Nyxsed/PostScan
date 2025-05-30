package ru.nyxsed.postscan.common.data.models.response.newsfeedget


import com.google.gson.annotations.SerializedName
import ru.nyxsed.postscan.common.data.models.response.ErrorResponse

data class WallGetResponse(
    @SerializedName("response")
    val content: WallGetContentResponse?,
    @SerializedName("error")
    val error: ErrorResponse?,
)