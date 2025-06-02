package ru.nyxsed.postscan.core.data.mapper

import ru.nyxsed.postscan.core.data.models.entity.ContentEntity
import ru.nyxsed.postscan.core.domain.models.entity.Content

object ContentMapper {
    fun ContentEntity.toDomain(): Content {
        return Content(
            contentId = contentId,
            ownerId = ownerId,
            type = type,
            isLiked = isLiked,
            urlSmall = urlSmall,
            urlMedium = urlMedium,
            urlBig = urlBig,
            title = title,
        )
    }

    fun Content.toEntity(): ContentEntity {
        return ContentEntity(
            contentId = contentId,
            ownerId = ownerId,
            type = type,
            isLiked = isLiked,
            urlSmall = urlSmall,
            urlMedium = urlMedium,
            urlBig = urlBig,
            title = title,
        )
    }
}