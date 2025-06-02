package ru.nyxsed.postscan.features.posts.domain.usecase

import ru.nyxsed.postscan.core.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class GetPostsForGroupUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(group: GroupEntity) = vkRepository.getPostsForGroup(group)
}

