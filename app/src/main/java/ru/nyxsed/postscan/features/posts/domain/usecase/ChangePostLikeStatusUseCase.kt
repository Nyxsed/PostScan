package ru.nyxsed.postscan.features.posts.domain.usecase

import ru.nyxsed.postscan.common.domain.models.entity.PostEntity
import ru.nyxsed.postscan.common.domain.repository.VkRepository


class ChangePostLikeStatusUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(post: PostEntity) = vkRepository.changePostLikeStatus(post)
}

