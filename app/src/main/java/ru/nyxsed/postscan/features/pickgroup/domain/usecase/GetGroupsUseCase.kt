package ru.nyxsed.postscan.features.pickgroup.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class GetGroupsUseCase(private val vkRepository: VkRepository) {
    private val scope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(): StateFlow<List<Group>> {
        val token = vkRepository.getAccessToken()

        return flow {
            emit(vkRepository.groupsGet(token))
        }
            .retry(2)
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )
    }
}