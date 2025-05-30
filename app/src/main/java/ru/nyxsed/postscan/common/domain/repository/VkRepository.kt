package ru.nyxsed.postscan.common.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ru.nyxsed.postscan.common.domain.models.entity.CommentEntity
import ru.nyxsed.postscan.common.domain.models.entity.ContentEntity
import ru.nyxsed.postscan.common.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.common.domain.models.entity.PostEntity

interface VkRepository {
    fun getGroupsStateFlow(): StateFlow<List<GroupEntity>>
    suspend fun searchGroups(searchQuery: String): List<GroupEntity>
    suspend fun getPostsForGroup(groupEntity: GroupEntity): List<PostEntity>
    suspend fun getPostsForGroupDateInterval(groupEntity: GroupEntity, startDate: Long, endDate: Long): List<PostEntity>
    suspend fun changeLikeStatus(post: PostEntity)
    suspend fun changeLikeStatus(contentEntity: ContentEntity)
    suspend fun checkLikeStatus(contentEntity: ContentEntity): Boolean
    fun getCommentsStateFlow(post: PostEntity): StateFlow<List<CommentEntity>>
}