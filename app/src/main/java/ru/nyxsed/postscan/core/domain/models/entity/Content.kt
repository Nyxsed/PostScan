package ru.nyxsed.postscan.core.domain.models.entity

data class Content(
    val contentId: Long,
    val ownerId: Long,
    val type: String,
    var isLiked : Boolean = false,
    val urlSmall: String,
    val urlMedium: String,
    val urlBig: String,
    val title: String,
)
