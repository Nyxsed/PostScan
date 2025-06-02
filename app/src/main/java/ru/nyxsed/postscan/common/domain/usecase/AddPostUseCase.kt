package ru.nyxsed.postscan.common.domain.usecase

import ru.nyxsed.postscan.common.domain.models.entity.PostEntity
import ru.nyxsed.postscan.common.domain.repository.DbRepository

class AddPostUseCase(private val dbRepository: DbRepository) {
    suspend operator fun invoke(post: PostEntity) = dbRepository.addPost(post)
}