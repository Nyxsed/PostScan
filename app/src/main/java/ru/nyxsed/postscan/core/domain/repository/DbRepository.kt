package ru.nyxsed.postscan.core.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow
import ru.nyxsed.postscan.core.domain.models.entity.Group
import ru.nyxsed.postscan.core.domain.models.entity.Post

interface DbRepository {
    fun getAllPosts(): StateFlow<List<Post>>
    suspend fun addPost(post: Post)
    suspend fun deletePost(post: Post)
    suspend fun deleteAllPostsForGroup(group: Group)
    suspend fun updatePost(post: Post)
    fun getAllGroups(): StateFlow<List<Group>>
    suspend fun addGroup(group: Group)
    suspend fun deleteGroup(group: Group)
    suspend fun updateGroup(group: Group)
    suspend fun deleteAllPosts()
    fun exportDatabase(uri: Uri): Boolean
    fun importDatabase(uri: Uri) : Boolean
}