package ru.nyxsed.postscan.common.data.models.response.wallgetcomments


import com.google.gson.annotations.SerializedName
import ru.nyxsed.postscan.common.data.models.response.newsfeedget.AttachmentResponse

data class ItemResponse(
    @SerializedName("attachments")
    val attachments: List<AttachmentResponse>?,
    @SerializedName("date")
    val date: Long,
    @SerializedName("from_id")
    val fromId: Long,
    @SerializedName("id")
    val commentId: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("post_id")
    val postId: Long,
    @SerializedName("text")
    val text: String,
    @SerializedName("parents_stack")
    val parentsStack: List<Long>,
    @SerializedName("thread")
    val thread: ThreadResponse?
)