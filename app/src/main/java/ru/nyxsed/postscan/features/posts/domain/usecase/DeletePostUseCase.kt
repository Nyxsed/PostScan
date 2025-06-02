package ru.nyxsed.postscan.features.posts.domain.usecase

import ru.nyxsed.postscan.core.domain.models.entity.PostEntity
import ru.nyxsed.postscan.core.domain.repository.DbRepository

class DeletePostUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke(post: PostEntity) = dbRepository.deletePost(post)
}