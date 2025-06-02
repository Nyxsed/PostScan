package ru.nyxsed.postscan.core.domain.models

data class Post(
    val postId : Long,
    val ownerId: Long,
    val ownerName: String,
    val ownerImageUrl: String,
    val publicationDate: Long,
    val contentText: String,
    val content: List<Content>,
    var isLiked: Boolean,
    val haveReposts: Boolean,
)