package ru.nyxsed.postscan.common.domain.models.entity

data class ContentEntity(
    val contentId: Long,
    val ownerId: Long,
    val type: String,
    var isLiked : Boolean = false,
    val urlSmall: String,
    val urlMedium: String,
    val urlBig: String,
    val title: String,
)
