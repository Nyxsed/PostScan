package ru.nyxsed.postscan.features.pickgroup.domain.usecase

import ru.nyxsed.postscan.common.domain.repository.VkRepository

class SearchGroupsUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(searchQuery: String) = vkRepository.searchGroups(searchQuery)
}