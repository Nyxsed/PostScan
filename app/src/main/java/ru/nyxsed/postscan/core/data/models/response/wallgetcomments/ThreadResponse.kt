package ru.nyxsed.postscan.core.data.models.response.wallgetcomments


import com.google.gson.annotations.SerializedName

data class ThreadResponse(
    @SerializedName("items")
    val items: List<ItemResponse>
)