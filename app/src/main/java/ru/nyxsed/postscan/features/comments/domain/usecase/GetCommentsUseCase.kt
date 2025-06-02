package ru.nyxsed.postscan.features.comments.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import ru.nyxsed.postscan.core.domain.models.Comment
import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class GetCommentsUseCase(private val vkRepository: VkRepository) {
    operator fun invoke(post: Post):  StateFlow<List<Comment>> {
        return vkRepository.getCommentsStateFlow(post)
    }
}