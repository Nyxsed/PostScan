package ru.nyxsed.postscan.core.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ru.nyxsed.postscan.core.domain.models.Comment
import ru.nyxsed.postscan.core.domain.models.Content
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.Post

interface VkRepository {
    fun getGroupsStateFlow(): StateFlow<List<Group>>
    suspend fun searchGroups(searchQuery: String): List<Group>
    suspend fun getPostsForGroup(group: Group): List<Post>
    suspend fun getPostsForGroupDateInterval(group: Group, startDate: Long, endDate: Long): List<Post>
    suspend fun changePostLikeStatus(post: Post)
    suspend fun changeContentLikeStatus(content: Content)
    suspend fun checkContentLikeStatus(content: Content): Boolean
    fun getCommentsStateFlow(post: Post): StateFlow<List<Comment>>
}