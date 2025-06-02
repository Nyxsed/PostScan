package ru.nyxsed.postscan.features.pickgroup.presentation

import ru.nyxsed.postscan.core.domain.models.Group

sealed class PickGroupState {
    data class Search(
        val groups: List<Group> = emptyList(),
        val existingGroups: List<Group> = emptyList(),
    ) : PickGroupState()

    data class User(
        val groups: List<Group> = emptyList(),
        val existingGroups: List<Group> = emptyList(),
    ) : PickGroupState()

    object Loading : PickGroupState()
}