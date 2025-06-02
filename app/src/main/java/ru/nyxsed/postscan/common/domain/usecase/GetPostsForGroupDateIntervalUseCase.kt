package ru.nyxsed.postscan.common.domain.usecase

import ru.nyxsed.postscan.common.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.common.domain.repository.VkRepository

class GetPostsForGroupDateIntervalUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(groupEntity: GroupEntity, startDate: Long, endDate: Long) =
        vkRepository.getPostsForGroupDateInterval(groupEntity = groupEntity, startDate = startDate, endDate = endDate)
}