package ru.nyxsed.postscan.features.pickgroup.domain.usecase

import ru.nyxsed.postscan.common.domain.repository.VkRepository

class GetGroupsUseCase(private val vkRepository: VkRepository) {
    operator fun invoke() = vkRepository.getGroupsStateFlow()
}