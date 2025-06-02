package ru.nyxsed.postscan.features.posts.domain.usecase

import ru.nyxsed.postscan.core.domain.models.entity.Post
import ru.nyxsed.postscan.core.domain.repository.VkRepository


class ChangePostLikeStatusUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(post: Post) = vkRepository.changePostLikeStatus(post)
}

