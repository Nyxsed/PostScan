package ru.nyxsed.postscan.core.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ru.nyxsed.postscan.core.data.database.DbDao
import ru.nyxsed.postscan.core.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.core.domain.models.entity.PostEntity
import ru.nyxsed.postscan.core.domain.repository.DbRepository
import java.io.File
import java.io.IOException

class DbRepositoryImpl(
    private val dbDao: DbDao,
    private val context: Context,
) : DbRepository {

    val scope = CoroutineScope(Dispatchers.Default)

    // posts
    override fun getAllPosts(): StateFlow<List<PostEntity>> =
        dbDao.getAllPosts()
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    override suspend fun addPost(post: PostEntity) {
        dbDao.insertPost(post)
    }

    override suspend fun deletePost(post: PostEntity) {
        dbDao.deletePost(post)
    }

    override suspend fun deleteAllPostsForGroup(group: GroupEntity) {
        dbDao.deleteAllPostsForGroup(group.groupId)
    }

    override suspend fun updatePost(post: PostEntity) {
        dbDao.updatePost(post)
    }

    // groups
    override fun getAllGroups(): StateFlow<List<GroupEntity>> =
        dbDao.getAllGroups()
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    override suspend fun addGroup(group: GroupEntity) {
        dbDao.insertGroup(group)
    }

    override suspend fun deleteGroup(group: GroupEntity) {
        dbDao.deleteGroup(group)
    }

    override suspend fun updateGroup(group: GroupEntity) {
        dbDao.updateGroup(group)
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

    override fun importDatabase(uri: Uri) : Boolean {
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