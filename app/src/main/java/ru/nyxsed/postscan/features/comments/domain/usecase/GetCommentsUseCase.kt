package ru.nyxsed.postscan.features.comments.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import ru.nyxsed.postscan.core.domain.models.Comment
import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class GetCommentsUseCase(private val vkRepository: VkRepository) {

    operator fun invoke(post: Post): Flow<List<Comment>> = flow {
        val token = vkRepository.getAccessToken()
        val comments = vkRepository.getComments(post.ownerId, post.postId, token)
        emit(comments)
    }.retry(2)
}