package ru.nyxsed.postscan.features.comments.presentation

import androidx.lifecycle.ViewModel
import ru.nyxsed.postscan.common.domain.models.SettingKey
import ru.nyxsed.postscan.common.domain.models.entity.PostEntity
import ru.nyxsed.postscan.common.domain.usecase.GetSettingUseCase
import ru.nyxsed.postscan.features.comments.domain.usecase.GetCommentsUseCase

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