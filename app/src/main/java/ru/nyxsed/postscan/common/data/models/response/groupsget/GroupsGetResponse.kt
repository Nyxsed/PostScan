package ru.nyxsed.postscan.common.data.models.response.groupsget


import com.google.gson.annotations.SerializedName
import ru.nyxsed.postscan.common.data.models.response.ErrorResponse

data class GroupsGetResponse(
    @SerializedName("response")
    val response: GroupsGetContentResponse?,
    @SerializedName("error")
    val error: ErrorResponse?,
)