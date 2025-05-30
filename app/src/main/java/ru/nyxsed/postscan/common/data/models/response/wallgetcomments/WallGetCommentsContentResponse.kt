package ru.nyxsed.postscan.common.data.models.response.wallgetcomments


import com.google.gson.annotations.SerializedName

data class WallGetCommentsContentResponse(
    @SerializedName("items")
    val items: List<ItemResponse>,
    @SerializedName("profiles")
    val profiles: List<ProfilesResponse>
)