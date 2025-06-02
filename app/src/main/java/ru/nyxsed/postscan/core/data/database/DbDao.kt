package ru.nyxsed.postscan.core.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.nyxsed.postscan.core.data.models.entity.GroupEntity
import ru.nyxsed.postscan.core.data.models.entity.PostEntity

@Dao
interface DbDao {
    // posts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Delete
    suspend fun deletePost(post: PostEntity)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("SELECT * FROM posts")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("DELETE FROM posts where ABS(ownerId) = :groupId")
    suspend fun deleteAllPostsForGroup(groupId : Long)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    // groups
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Delete
    suspend fun deleteGroup(group: GroupEntity)

    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Query("SELECT * FROM groups")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups where groupId = :groupId")
    suspend fun getGroupById(groupId : Long): GroupEntity?
}