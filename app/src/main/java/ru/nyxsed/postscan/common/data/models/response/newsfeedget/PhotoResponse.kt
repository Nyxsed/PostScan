package ru.nyxsed.postscan.common.data.models.response.newsfeedget


import com.google.gson.annotations.SerializedName

data class PhotoResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("sizes")
    val sizes: List<SizeResponse>,
)