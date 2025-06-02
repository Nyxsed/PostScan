package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class GetPostsForGroupDateIntervalUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(groupEntity: GroupEntity, startDate: Long, endDate: Long) =
        vkRepository.getPostsForGroupDateInterval(groupEntity = groupEntity, startDate = startDate, endDate = endDate)
}