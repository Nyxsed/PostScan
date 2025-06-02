package ru.nyxsed.postscan.features.posts.domain.usecase

import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.domain.repository.VkRepository


class ChangePostLikeStatusUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(post: Post) {
        val token = vkRepository.getAccessToken()

        if (!post.isLiked) {
            vkRepository.addLike(post.ownerId, post.postId, "post", token)
        } else {
            vkRepository.deleteLike(post.ownerId, post.postId, "post", token)
        }
    }
}

