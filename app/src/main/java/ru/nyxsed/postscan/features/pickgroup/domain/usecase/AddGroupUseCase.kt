package ru.nyxsed.postscan.features.pickgroup.domain.usecase

import ru.nyxsed.postscan.common.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.common.domain.repository.DbRepository

class AddGroupUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke(group: GroupEntity) = dbRepository.addGroup(group)
}