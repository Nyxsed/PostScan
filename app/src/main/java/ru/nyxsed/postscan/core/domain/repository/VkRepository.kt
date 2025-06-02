package ru.nyxsed.postscan.core.domain.repository

import ru.nyxsed.postscan.core.data.models.response.groupsget.GroupsGetResponse
import ru.nyxsed.postscan.core.data.models.response.newsfeedget.WallGetResponse
import ru.nyxsed.postscan.core.domain.models.Comment
import ru.nyxsed.postscan.core.domain.models.Group

interface VkRepository {
    fun getAccessToken(): String
    suspend fun isContentLiked(ownerId: Long, itemId: Long, type: String, token: String): Boolean
    suspend fun getComments(ownerId: Long, postId: Long, token: String): List<Comment>
    suspend fun addLike(ownerId: Long, itemId: Long, type: String, token: String)
    suspend fun deleteLike(ownerId: Long, itemId: Long, type: String, token: String)
    suspend fun wallGetPosts(ownerId: String, offset: Int, token: String): WallGetResponse
    suspend fun groupsGetById(token: String, groupId: String): GroupsGetResponse
    suspend fun groupsSearch(token: String, searchQuery: String): GroupsGetResponse
    suspend fun groupsGet(token: String): List<Group>
}