package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.repository.DbRepository

class DeleteGroupPostsUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke(group: Group) = dbRepository.deleteAllPostsForGroup(group)
}