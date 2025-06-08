package ru.nyxsed.postscan.features.comments.presentation

import ru.nyxsed.postscan.core.domain.models.Content

sealed class CommentsIntent {
    data class OnMihonClicked(val text: String) : CommentsIntent()
    data class OnTextLongClick(val text: String) : CommentsIntent()
    data class OnImageClicked(val list: List<Content>, val index: Int) : CommentsIntent()
}