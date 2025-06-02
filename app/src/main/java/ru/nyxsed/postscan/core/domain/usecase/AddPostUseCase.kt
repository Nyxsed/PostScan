package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.domain.repository.DbRepository

class AddPostUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke(post: Post) = dbRepository.addPost(post)
}