package ru.nyxsed.postscan.core.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.nyxsed.postscan.core.data.database.DbDao
import ru.nyxsed.postscan.core.data.mapper.ContentMapper.toDomain
import ru.nyxsed.postscan.core.data.mapper.ContentMapper.toEntity
import ru.nyxsed.postscan.core.data.mapper.GroupMapper.toDomain
import ru.nyxsed.postscan.core.data.mapper.GroupMapper.toEntity
import ru.nyxsed.postscan.core.data.mapper.PostMapper.toDomain
import ru.nyxsed.postscan.core.data.mapper.PostMapper.toEntity
import ru.nyxsed.postscan.core.data.models.entity.ContentEntity
import ru.nyxsed.postscan.core.data.models.entity.PostEntity
import ru.nyxsed.postscan.core.domain.models.entity.Content
import ru.nyxsed.postscan.core.domain.models.entity.Group
import ru.nyxsed.postscan.core.domain.models.entity.Post
import ru.nyxsed.postscan.core.domain.repository.DbRepository
import java.io.File
import java.io.IOException
import kotlin.collections.map

class DbRepositoryImpl(
    private val dbDao: DbDao,
    private val context: Context,
) : DbRepository {

    val scope = CoroutineScope(Dispatchers.Default)

    // posts
    override fun getAllPosts(): StateFlow<List<Post>> =
        dbDao.getAllPosts()
            .map { entityList ->
                entityList.map { postEntity ->
                    val contentList: List<Content> = postEntity.content.map { it.toDomain() }
                    postEntity.toDomain(contentList)
                }
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    override suspend fun addPost(post: Post) {
        val contentList: List<ContentEntity> = post.content.map {
            it.toEntity()
        }
        val postEntity: PostEntity = post.toEntity(contentList)
        dbDao.insertPost(postEntity)
    }

    override suspend fun deletePost(post: Post) {
        val contentList: List<ContentEntity> = post.content.map {
            it.toEntity()
        }
        val postEntity: PostEntity = post.toEntity(contentList)
        dbDao.deletePost(postEntity)
    }

    override suspend fun deleteAllPostsForGroup(group: Group) {
        dbDao.deleteAllPostsForGroup(group.groupId)
    }

    override suspend fun updatePost(post: Post) {
        val contentList: List<ContentEntity> = post.content.map {
            it.toEntity()
        }
        val postEntity: PostEntity = post.toEntity(contentList)
        dbDao.updatePost(postEntity)
    }

    // groups
    override fun getAllGroups(): StateFlow<List<Group>> =
        dbDao.getAllGroups()
            .map { entityList ->
                entityList.map {
                    it.toDomain()
                }
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    override suspend fun addGroup(group: Group) {

        dbDao.insertGroup(group.toEntity())
    }

    override suspend fun deleteGroup(group: Group) {
        dbDao.deleteGroup(group.toEntity())
    }

    override suspend fun updateGroup(group: Group) {
        dbDao.updateGroup(group.toEntity())
    }

    override suspend fun deleteAllPosts() {
        dbDao.deleteAllPosts()
    }

    // export import
    override fun exportDatabase(uri: Uri): Boolean {
        val dbFile = File(context.getDatabasePath("app_database").absolutePath)

        if (!dbFile.exists()) {
            return false
        }

        try {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                dbFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    override fun importDatabase(uri: Uri): Boolean {
        val dbPath = context.getDatabasePath("app_database")
        context.deleteDatabase("app_database")

        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                dbPath.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }
}