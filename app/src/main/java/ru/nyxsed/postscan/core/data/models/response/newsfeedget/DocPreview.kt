package ru.nyxsed.postscan.core.data.models.response.newsfeedget

import com.google.gson.annotations.SerializedName

data class DocPreview(
    @SerializedName("photo")
    val photo: DocPhotoResponse,
)