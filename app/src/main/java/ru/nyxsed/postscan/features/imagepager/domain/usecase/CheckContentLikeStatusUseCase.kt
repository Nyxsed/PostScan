package ru.nyxsed.postscan.features.imagepager.domain.usecase

import ru.nyxsed.postscan.core.domain.models.entity.Content
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class CheckContentLikeStatusUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(content: Content) = vkRepository.checkContentLikeStatus(content)
}