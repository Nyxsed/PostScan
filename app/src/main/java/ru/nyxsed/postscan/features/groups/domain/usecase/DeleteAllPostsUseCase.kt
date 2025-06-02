package ru.nyxsed.postscan.features.groups.domain.usecase

import ru.nyxsed.postscan.core.domain.repository.DbRepository

class DeleteAllPostsUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke() = dbRepository.deleteAllPosts()
}