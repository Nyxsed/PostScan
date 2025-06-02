package ru.nyxsed.postscan.features.imagepager.domain.usecase

import ru.nyxsed.postscan.core.domain.models.Content
import ru.nyxsed.postscan.core.domain.repository.VkRepository


class ChangeContentLikeStatusUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(content: Content) {
        val token = vkRepository.getAccessToken()
        val type = if (content.type == "album") "photo" else content.type

        if (!content.isLiked) {
            vkRepository.addLike(content.ownerId, content.contentId, type, token)
        } else {
            vkRepository.deleteLike(content.ownerId, content.contentId, type, token)
        }
    }
}