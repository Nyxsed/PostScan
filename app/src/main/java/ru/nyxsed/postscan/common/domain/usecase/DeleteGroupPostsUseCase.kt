package ru.nyxsed.postscan.common.domain.usecase

import ru.nyxsed.postscan.common.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.common.domain.repository.DbRepository

class DeleteGroupPostsUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke(group: GroupEntity) = dbRepository.deleteAllPostsForGroup(group)
}