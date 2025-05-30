package ru.nyxsed.postscan.common.data.models.response.newsfeedget

import com.google.gson.annotations.SerializedName

data class DocPhotoResponse(
    @SerializedName("sizes")
    val sizes: List<DocSizeResponse>,
)