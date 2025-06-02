package ru.nyxsed.postscan.features.posts.domain.usecase

import kotlinx.coroutines.delay
import ru.nyxsed.postscan.core.data.mapper.VkMapper
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.repository.DataStoreRepository
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class GetPostsForGroupUseCase(
    private val vkRepository: VkRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val mapper: VkMapper,
) {
    suspend operator fun invoke(group: Group): List<Post> {
        var offset: Int = 0
        val posts = mutableListOf<Post>()
        val lastFetchDate = group.lastFetchDate
        val notLoadLikedPosts = dataStoreRepository.getBoolean(SettingKey.NOT_LOAD_LIKED_POSTS)
        val token = vkRepository.getAccessToken()
        val ownerId = (group.groupId.times(-1)).toString()

        while (true) {
            val response = vkRepository.wallGetPosts(
                token = token,
                ownerId = ownerId,
                offset = offset
            )
            val error = response.error?.errorMsg
            if (error != null) {
                throw Exception(error)
            }

            if (response.content == null || response.content.items.isNullOrEmpty()) break

            val responsePosts = mapper.mapWallGetResponseToPosts(response)
            responsePosts
                .filter { it.publicationDate > lastFetchDate }
                .filter { if (notLoadLikedPosts) !it.isLiked else true }
                .forEach { posts.add(it) }

            if (responsePosts.last().publicationDate <= lastFetchDate) break

            offset += 100
            delay(350)
        }
        return posts.toList()
    }
}

