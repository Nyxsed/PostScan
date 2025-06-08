package ru.nyxsed.postscan.features.comments.presentation

import ru.nyxsed.postscan.core.domain.models.Comment

data class CommentsState(
    val comments: List<Comment> = emptyList(),
    val settingMihon: Boolean = false,
)
