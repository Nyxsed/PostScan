package ru.nyxsed.postscan.features.comments.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import ru.nyxsed.postscan.core.domain.models.Comment
import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class GetCommentsUseCase(private val vkRepository: VkRepository) {

    private val scope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(post: Post): StateFlow<List<Comment>> {
        val token = vkRepository.getAccessToken()

        return flow {
            emit(vkRepository.getComments(post.ownerId, post.postId, token))
        }
            .retry(2)
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )
    }
}