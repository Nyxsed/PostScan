package ru.nyxsed.postscan.core.data.repository

import com.vk.id.VKID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import ru.nyxsed.postscan.core.data.mapper.VkMapper
import ru.nyxsed.postscan.core.data.network.ApiService
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.models.entity.Content
import ru.nyxsed.postscan.core.domain.models.entity.Group
import ru.nyxsed.postscan.core.domain.models.entity.Post
import ru.nyxsed.postscan.core.domain.repository.DataStoreRepository
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class VkRepositoryImpl(
    private val apiService: ApiService,
    private val mapper: VkMapper,
    private val dataStoreRepository: DataStoreRepository,
) : VkRepository {

    val scope = CoroutineScope(Dispatchers.Default)

    private fun getAccessToken(): String {
        return VKID.Companion.instance.accessToken?.token ?: throw IllegalStateException("Token is null")
    }

    // groups TODO обертку для состояния
    override fun getGroupsStateFlow() =
        flow {
            val response = apiService.groupsGet(
                token = getAccessToken()
            )
            emit(mapper.mapGroupsGetResponseToGroups(response))
        }
            .retry(2)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = listOf()
            )

    override suspend fun searchGroups(searchQuery: String): List<Group> {
        val result = mutableListOf<Group>()


        val responseById = apiService.groupsGetById(
            token = getAccessToken(),
            groupId = searchQuery
        )
        val errorById = responseById.error?.errorMsg
        result.addAll(mapper.mapGroupsGetResponseToGroups(responseById))

        val responseSearch = apiService.groupsSearch(
            token = getAccessToken(),
            searchQuery = searchQuery
        )
        val errorSearch = responseSearch.error?.errorMsg

        if (errorById != null && errorSearch != null) {
            throw Exception("$errorById || $errorSearch")
        }

        result.addAll(mapper.mapGroupsGetResponseToGroups(responseSearch))

        return result.distinct()
    }

    // post
    override suspend fun getPostsForGroup(group: Group): List<Post> {
        var offset: Int = 0
        val posts = mutableListOf<Post>()
        val lastFetchDate = group.lastFetchDate
        val notLoadLikedPosts = dataStoreRepository.getBoolean(SettingKey.NOT_LOAD_LIKED_POSTS)

        while (true) {
            val response = apiService.wallGet(
                token = getAccessToken(),
                ownerId = (group.groupId.times(-1)).toString(),
                offset = offset
            )
            val error = response.error?.errorMsg
            if (error != null) {
                throw Exception(error)
            }

            if (response.content == null || response.content.items.isNullOrEmpty()) break

            val responsePosts = mapper.mapWallGetResponseToPosts(response)
            responsePosts
                .filter {
                    it.publicationDate > lastFetchDate
                }
                .filter {
                    if (notLoadLikedPosts) {
                        it.isLiked == false
                    } else {
                        true
                    }
                }
                .forEach {
                    posts.add(it)
                }

            if (responsePosts.last().publicationDate <= lastFetchDate) break

            offset += 100
            delay(350)
        }
        return posts.toList()
    }

    // post
    override suspend fun getPostsForGroupDateInterval(group: Group, startDate: Long, endDate: Long): List<Post> {
        var offset: Int = 0
        val posts = mutableListOf<Post>()
        val notLoadLikedPosts = dataStoreRepository.getBoolean(SettingKey.NOT_LOAD_LIKED_POSTS)

        while (true) {
            val response = apiService.wallGet(
                token = getAccessToken(),
                ownerId = (group.groupId.times(-1)).toString(),
                offset = offset
            )
            val error = response.error?.errorMsg
            if (error != null) {
                throw Exception(error)
            }

            if (response.content == null || response.content.items.isNullOrEmpty()) break

            val responsePosts = mapper.mapWallGetResponseToPosts(response)

            responsePosts
                .filter {
                    startDate <= it.publicationDate && it.publicationDate <= endDate
                }
                .filter {
                    if (notLoadLikedPosts) {
                        it.isLiked == false
                    } else {
                        true
                    }
                }
                .forEach {
                    posts.add(it)
                }

            if (responsePosts.last().publicationDate < startDate) break

            offset += 100
            delay(350)
        }
        return posts.toList()
    }

    override suspend fun changePostLikeStatus(post: Post) {
        val response = if (!post.isLiked) {
            apiService.addLike(
                token = getAccessToken(),
                ownerId = post.ownerId,
                itemId = post.postId,
                type = "post"
            )
        } else {
            apiService.deleteLike(
                token = getAccessToken(),
                ownerId = post.ownerId,
                itemId = post.postId,
                type = "post"
            )
        }
        val error = response.error?.errorMsg
        if (error != null) {
            throw Exception(error)
        }
    }

    // content
    override suspend fun changeContentLikeStatus(content: Content) {
        val response = if (!content.isLiked) {
            apiService.addLike(
                token = getAccessToken(),
                ownerId = content.ownerId,
                itemId = content.contentId,
                type = if (content.type == "album") "photo" else content.type
            )
        } else {
            apiService.deleteLike(
                token = getAccessToken(),
                ownerId = content.ownerId,
                itemId = content.contentId,
                type = if (content.type == "album") "photo" else content.type
            )
        }
        val error = response.error?.errorMsg
        if (error != null) {
            throw Exception(error)
        }
    }

    override suspend fun checkContentLikeStatus(content: Content): Boolean {
        val response = apiService.isLiked(
            token = getAccessToken(),
            ownerId = content.ownerId,
            itemId = content.contentId,
            type = if (content.type == "album") "photo" else content.type
        )
        return response.response?.liked == 1
    }

    // comments TODO обертку для состояния
    override fun getCommentsStateFlow(post: Post) =
        flow {
            val response = apiService.wallGetComments(
                token = getAccessToken(),
                ownerId = post.ownerId,
                postId = post.postId
            )
            emit(mapper.mapWallGetCommentsResponseToComments(response))
        }
            .retry(2)
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

}