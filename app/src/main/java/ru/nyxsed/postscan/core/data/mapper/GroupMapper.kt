package ru.nyxsed.postscan.core.data.mapper

import ru.nyxsed.postscan.core.data.models.entity.GroupEntity
import ru.nyxsed.postscan.core.domain.models.entity.Group

object GroupMapper {
    fun GroupEntity.toDomain(): Group {
        return Group(
            groupId = groupId,
            name = name,
            screenName = screenName,
            avatarUrl = avatarUrl,
            lastFetchDate = lastFetchDate
        )
    }

    fun Group.toEntity(): GroupEntity {
        return GroupEntity(
            groupId = groupId,
            name = name,
            screenName = screenName,
            avatarUrl = avatarUrl,
            lastFetchDate = lastFetchDate
        )
    }
}