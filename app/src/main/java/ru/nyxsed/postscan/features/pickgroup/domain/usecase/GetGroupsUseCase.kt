package ru.nyxsed.postscan.features.pickgroup.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class GetGroupsUseCase(private val vkRepository: VkRepository) {
    operator fun invoke(): Flow<List<Group>> {
        val token = vkRepository.getAccessToken()
        return flow {
            emit(vkRepository.groupsGet(token))
        }.retry(2)
    }
}