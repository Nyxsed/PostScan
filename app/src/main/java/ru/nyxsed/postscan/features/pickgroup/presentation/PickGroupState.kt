package ru.nyxsed.postscan.features.pickgroup.presentation

import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.PickGroupMode

data class PickGroupState (
    val mode : PickGroupMode = PickGroupMode.LOADING,
    val existingGroups: List<Group> = emptyList(),
    val fetchedGroups: List<Group> = emptyList(),
    val searchQuery: String = "",
    val showDeleteDialog: Boolean = false,
    val groupToDelete: Group? = null
)