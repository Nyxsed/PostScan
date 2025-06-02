package ru.nyxsed.postscan.features.posts.domain.usecase

import ru.nyxsed.postscan.core.domain.repository.DbRepository

class GetAllPostsUseCase(private val dbRepository: DbRepository) {
    operator fun invoke() = dbRepository.getAllPosts()
}