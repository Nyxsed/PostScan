package ru.nyxsed.postscan.features.pickgroup.domain.usecase

import ru.nyxsed.postscan.core.domain.models.entity.Group
import ru.nyxsed.postscan.core.domain.repository.DbRepository

class AddGroupUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke(group: Group) = dbRepository.addGroup(group)
}