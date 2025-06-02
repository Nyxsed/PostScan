package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.models.entity.Group
import ru.nyxsed.postscan.core.domain.repository.DbRepository

class DeleteGroupPostsUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke(group: Group) = dbRepository.deleteAllPostsForGroup(group)
}