package ru.nyxsed.postscan.features.imagepager.domain.usecase

import ru.nyxsed.postscan.core.domain.models.Content
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class CheckContentLikeStatusUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(content: Content): Boolean {
        val token = vkRepository.getAccessToken()
        val contentType = if (content.type == "album") "photo" else content.type

        return vkRepository.isContentLiked(
            ownerId = content.ownerId,
            itemId = content.contentId,
            type = contentType,
            token = token,
        )
    }
}