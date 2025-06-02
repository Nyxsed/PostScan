package ru.nyxsed.postscan.core.data.repository

import com.vk.id.VKID
import ru.nyxsed.postscan.core.data.mapper.VkMapper
import ru.nyxsed.postscan.core.data.models.response.groupsget.GroupsGetResponse
import ru.nyxsed.postscan.core.data.models.response.newsfeedget.WallGetResponse
import ru.nyxsed.postscan.core.data.network.ApiService
import ru.nyxsed.postscan.core.domain.models.Comment
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class VkRepositoryImpl(
    private val apiService: ApiService,
    private val mapper: VkMapper,
) : VkRepository {

    override fun getAccessToken(): String {
        return VKID.Companion.instance.accessToken?.token ?: throw IllegalStateException("Token is null")
    }

    override suspend fun groupsGet(
        token: String,
    ): List<Group> {
        val response = apiService.groupsGet(
            token = token,
        )
        return mapper.mapGroupsGetResponseToGroups(response)
    }

    override suspend fun groupsSearch(
        token: String,
        searchQuery: String,
    ): GroupsGetResponse {
        return apiService.groupsSearch(token, searchQuery)
    }

    override suspend fun groupsGetById(
        token: String,
        groupId: String,
    ): GroupsGetResponse {
        return apiService.groupsGetById(token, groupId)
    }

    override suspend fun wallGetPosts(
        ownerId: String,
        offset: Int,
        token: String,
    ): WallGetResponse {
        return apiService.wallGet(token, ownerId, offset)
    }

    override suspend fun addLike(ownerId: Long, itemId: Long, type: String, token: String) {
        val response = apiService.addLike(
            token = getAccessToken(),
            ownerId = ownerId,
            itemId = itemId,
            type = type
        )
        val error = response.error?.errorMsg
        if (error != null) {
            throw Exception(error)
        }
    }

    override suspend fun deleteLike(ownerId: Long, itemId: Long, type: String, token: String) {
        val response = apiService.deleteLike(
            token = getAccessToken(),
            ownerId = ownerId,
            itemId = itemId,
            type = type
        )
        val error = response.error?.errorMsg
        if (error != null) {
            throw Exception(error)
        }
    }

    override suspend fun isContentLiked(
        ownerId: Long,
        itemId: Long,
        type: String,
        token: String,
    ): Boolean {
        val response = apiService.isLiked(
            token = token,
            ownerId = ownerId,
            itemId = itemId,
            type = type
        )
        return response.response?.liked == 1
    }

    override suspend fun getComments(
        ownerId: Long,
        postId: Long,
        token: String,
    ): List<Comment> {
        val response = apiService.wallGetComments(
            token = token,
            ownerId = ownerId,
            postId = postId
        )
        return mapper.mapWallGetCommentsResponseToComments(response)
    }
}