package ru.nyxsed.postscan.common.data.models.response.newsfeedget


import com.google.gson.annotations.SerializedName

data class AttachmentResponse(
    @SerializedName("type")
    val type: String,
    @SerializedName("photo")
    val photo: PhotoResponse?,
    @SerializedName("video")
    val video: VideoResponse?,
    @SerializedName("album")
    val album: AlbumResponse?,
    @SerializedName("doc")
    val doc: DocResponse?,
)