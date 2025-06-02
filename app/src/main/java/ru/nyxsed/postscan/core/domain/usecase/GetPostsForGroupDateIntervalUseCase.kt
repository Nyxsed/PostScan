package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.models.entity.Group
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class GetPostsForGroupDateIntervalUseCase(private val vkRepository: VkRepository) {
    suspend operator fun invoke(group: Group, startDate: Long, endDate: Long) =
        vkRepository.getPostsForGroupDateInterval(group = group, startDate = startDate, endDate = endDate)
}