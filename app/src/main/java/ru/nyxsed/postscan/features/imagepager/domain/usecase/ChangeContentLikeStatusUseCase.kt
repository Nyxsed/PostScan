package ru.nyxsed.postscan.features.imagepager.domain.usecase

import ru.nyxsed.postscan.core.domain.models.entity.Content
import ru.nyxsed.postscan.core.domain.repository.VkRepository


class ChangeContentLikeStatusUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(content: Content) = vkRepository.changeContentLikeStatus(content)
}