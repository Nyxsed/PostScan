package ru.nyxsed.postscan.presentation.screens.commentsscreen

import androidx.lifecycle.ViewModel
import ru.nyxsed.postscan.common.domain.repository.VkRepository
import ru.nyxsed.postscan.data.models.entity.PostEntity
import ru.nyxsed.postscan.util.DataStoreInteraction

class CommentsScreenViewModel(
    private val vkRepository: VkRepository,
    private val post: PostEntity,
    private val dataStoreInteraction: DataStoreInteraction,
) : ViewModel() {
    val comments = vkRepository.getCommentsStateFlow(post = post)

    suspend fun getSettingBoolean(key: String): Boolean {
        return dataStoreInteraction.getSettingBooleanFromDataStore(key)
    }
}