package ru.nyxsed.postscan.features.imagepager.domain.usecase

import ru.nyxsed.postscan.common.domain.models.entity.ContentEntity
import ru.nyxsed.postscan.common.domain.repository.VkRepository

class CheckLikeStatusUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(contentEntity: ContentEntity) = vkRepository.checkLikeStatus(contentEntity)
}