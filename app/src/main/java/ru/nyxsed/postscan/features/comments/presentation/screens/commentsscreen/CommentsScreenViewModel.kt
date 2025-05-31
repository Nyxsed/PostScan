package ru.nyxsed.postscan.features.comments.presentation.screens.commentsscreen

import androidx.lifecycle.ViewModel
import ru.nyxsed.postscan.common.domain.models.entity.PostEntity
import ru.nyxsed.postscan.features.comments.domain.usecase.GetCommentsUseCase
import ru.nyxsed.postscan.features.preferences.domain.model.SettingKey
import ru.nyxsed.postscan.features.preferences.domain.usecase.GetSettingUseCase

class CommentsScreenViewModel(
    private val post: PostEntity,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getSettingUseCase: GetSettingUseCase
) : ViewModel() {
    val comments = getCommentsUseCase(post)

    suspend fun getSettingBoolean(key: SettingKey): Boolean {
        return getSettingUseCase(key)
    }
}