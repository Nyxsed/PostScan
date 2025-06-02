package ru.nyxsed.postscan.common.domain.usecase

import ru.nyxsed.postscan.common.domain.repository.DbRepository

class GetAllGroupsUseCase(private val dbRepository: DbRepository) {
    operator fun invoke() = dbRepository.getAllGroups()
}