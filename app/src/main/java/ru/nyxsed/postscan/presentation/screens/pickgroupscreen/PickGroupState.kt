package ru.nyxsed.postscan.presentation.screens.pickgroupscreen

import ru.nyxsed.postscan.data.models.entity.GroupEntity

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