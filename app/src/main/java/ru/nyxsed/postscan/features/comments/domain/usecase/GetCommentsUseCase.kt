package ru.nyxsed.postscan.features.comments.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import ru.nyxsed.postscan.core.domain.models.entity.CommentEntity
import ru.nyxsed.postscan.core.domain.models.entity.PostEntity
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class GetCommentsUseCase(private val vkRepository: VkRepository) {
    operator fun invoke(postEntity: PostEntity):  StateFlow<List<CommentEntity>> {
        return vkRepository.getCommentsStateFlow(postEntity)
    }
}