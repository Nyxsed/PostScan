package ru.nyxsed.postscan.common.data.models.response.newsfeedget

import com.google.gson.annotations.SerializedName

data class DocResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("preview")
    val preview: DocPreview?,
)