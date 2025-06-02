package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.repository.DbRepository

class DeleteGroupUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke(group: Group) = dbRepository.deleteGroup(group)
}