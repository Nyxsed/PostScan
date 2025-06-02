package ru.nyxsed.postscan.features.posts.domain.usecase

import ru.nyxsed.postscan.common.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.common.domain.repository.VkRepository

class GetPostsForGroupUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(group: GroupEntity) = vkRepository.getPostsForGroup(group)
}

