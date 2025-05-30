package ru.nyxsed.postscan.common.presentation.screens.pickgroupscreen

import ru.nyxsed.postscan.common.domain.models.entity.GroupEntity

sealed class PickGroupState {
    data class Search(
        val groups: List<GroupEntity> = emptyList(),
        val existingGroups: List<GroupEntity> = emptyList(),
    ) : PickGroupState()

    data class User(
        val groups: List<GroupEntity> = emptyList(),
        val existingGroups: List<GroupEntity> = emptyList(),
    ) : PickGroupState()

    object Loading : PickGroupState()
}