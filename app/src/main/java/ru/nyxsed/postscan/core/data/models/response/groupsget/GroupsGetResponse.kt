package ru.nyxsed.postscan.core.data.models.response.groupsget


import com.google.gson.annotations.SerializedName
import ru.nyxsed.postscan.core.data.models.response.ErrorResponse

data class GroupsGetResponse(
    @SerializedName("response")
    val response: GroupsGetContentResponse?,
    @SerializedName("error")
    val error: ErrorResponse?,
)