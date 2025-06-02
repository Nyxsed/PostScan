package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.repository.DbRepository

class GetAllGroupsUseCase(private val dbRepository: DbRepository) {
    operator fun invoke() = dbRepository.getAllGroups()
}