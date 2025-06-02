package ru.nyxsed.postscan.core.data.mapper

import ru.nyxsed.postscan.core.data.models.entity.ContentEntity
import ru.nyxsed.postscan.core.data.models.entity.PostEntity
import ru.nyxsed.postscan.core.domain.models.Content
import ru.nyxsed.postscan.core.domain.models.Post

object PostMapper {
    fun PostEntity.toDomain(contentList: List<Content>): Post {
        return Post(
            postId = postId,
            ownerId = ownerId,
            ownerName = ownerName,
            ownerImageUrl = ownerImageUrl,
            publicationDate = publicationDate,
            contentText = contentText,
            content = contentList,
            isLiked = isLiked,
            haveReposts = haveReposts
        )
    }

    fun Post.toEntity(contentList: List<ContentEntity>): PostEntity {
        return PostEntity(
            postId = postId,
            ownerId = ownerId,
            ownerName = ownerName,
            ownerImageUrl = ownerImageUrl,
            publicationDate = publicationDate,
            contentText = contentText,
            content = contentList,
            isLiked = isLiked,
            haveReposts = haveReposts
        )
    }
}