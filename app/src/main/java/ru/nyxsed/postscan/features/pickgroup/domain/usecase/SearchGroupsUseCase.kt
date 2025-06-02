package ru.nyxsed.postscan.features.pickgroup.domain.usecase

import ru.nyxsed.postscan.core.data.mapper.VkMapper
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class SearchGroupsUseCase(
    private val vkRepository: VkRepository,
    private val mapper: VkMapper,
) {
    suspend operator fun invoke(searchQuery: String): List<Group> {
        val result = mutableListOf<Group>()
        val token = vkRepository.getAccessToken()

        val responseById = vkRepository.groupsGetById(
            token = token,
            groupId = searchQuery
        )
        val errorById = responseById.error?.errorMsg
        result.addAll(mapper.mapGroupsGetResponseToGroups(responseById))

        val responseSearch = vkRepository.groupsSearch(
            token = token,
            searchQuery = searchQuery
        )
        val errorSearch = responseSearch.error?.errorMsg

        if (errorById != null && errorSearch != null) {
            throw Exception("$errorById || $errorSearch")
        }

        result.addAll(mapper.mapGroupsGetResponseToGroups(responseSearch))

        return result.distinct()
    }
}