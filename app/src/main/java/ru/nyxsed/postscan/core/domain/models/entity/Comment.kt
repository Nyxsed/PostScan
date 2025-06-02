package ru.nyxsed.postscan.core.domain.models.entity

data class Comment(
    val commentId: Long,
    val ownerId: Long,
    val ownerName: String,
    val ownerImageUrl: String,
    val publicationDate: Long,
    val contentText: String,
    val content: List<Content>,
    val parentStack: Long?,
)
