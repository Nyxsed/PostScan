package ru.nyxsed.postscan.core.domain.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val postId : Long,
    val ownerId: Long,
    val ownerName: String,
    val ownerImageUrl: String,
    val publicationDate: Long,
    val contentText: String,
    val content: List<ContentEntity>,
    var isLiked: Boolean,
    val haveReposts: Boolean,
)
