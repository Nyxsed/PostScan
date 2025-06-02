package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.core.domain.repository.DbRepository

class DeleteGroupPostsUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke(group: GroupEntity) = dbRepository.deleteAllPostsForGroup(group)
}