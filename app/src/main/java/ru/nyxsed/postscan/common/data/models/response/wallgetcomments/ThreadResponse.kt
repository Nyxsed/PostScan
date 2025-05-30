package ru.nyxsed.postscan.common.data.models.response.wallgetcomments


import com.google.gson.annotations.SerializedName

data class ThreadResponse(
    @SerializedName("items")
    val items: List<ItemResponse>
)