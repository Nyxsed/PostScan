package ru.nyxsed.postscan.core.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    val groupId: Long,
    val name: String,
    val screenName: String,
    val avatarUrl: String,
    var lastFetchDate: Long,
)
