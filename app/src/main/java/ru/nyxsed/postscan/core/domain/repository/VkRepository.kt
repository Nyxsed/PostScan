package ru.nyxsed.postscan.core.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ru.nyxsed.postscan.core.domain.models.entity.CommentEntity
import ru.nyxsed.postscan.core.domain.models.entity.ContentEntity
import ru.nyxsed.postscan.core.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.core.domain.models.entity.PostEntity

interface VkRepository {
    fun getGroupsStateFlow(): StateFlow<List<GroupEntity>>
    suspend fun searchGroups(searchQuery: String): List<GroupEntity>
    suspend fun getPostsForGroup(groupEntity: GroupEntity): List<PostEntity>
    suspend fun getPostsForGroupDateInterval(groupEntity: GroupEntity, startDate: Long, endDate: Long): List<PostEntity>
    suspend fun changePostLikeStatus(post: PostEntity)
    suspend fun changeContentLikeStatus(contentEntity: ContentEntity)
    suspend fun checkContentLikeStatus(contentEntity: ContentEntity): Boolean
    fun getCommentsStateFlow(post: PostEntity): StateFlow<List<CommentEntity>>
}