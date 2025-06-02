package ru.nyxsed.postscan.core.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow
import ru.nyxsed.postscan.core.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.core.domain.models.entity.PostEntity

interface DbRepository {
    fun getAllPosts(): StateFlow<List<PostEntity>>
    suspend fun addPost(post: PostEntity)
    suspend fun deletePost(post: PostEntity)
    suspend fun deleteAllPostsForGroup(group: GroupEntity)
    suspend fun updatePost(post: PostEntity)
    fun getAllGroups(): StateFlow<List<GroupEntity>>
    suspend fun addGroup(group: GroupEntity)
    suspend fun deleteGroup(group: GroupEntity)
    suspend fun updateGroup(group: GroupEntity)
    suspend fun deleteAllPosts()
    fun exportDatabase(uri: Uri): Boolean
    fun importDatabase(uri: Uri) : Boolean
}