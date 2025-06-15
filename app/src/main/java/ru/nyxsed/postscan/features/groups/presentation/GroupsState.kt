package ru.nyxsed.postscan.features.groups.presentation

import ru.nyxsed.postscan.core.domain.models.Group

data class GroupsState(
    val groups: List<Group> = emptyList(),
    val showTutorial: Boolean = false,
    val groupToDelete: Group? = null,
    val showAddDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showDeleteAllDialog: Boolean = false,
    val showDownloadDialog: Boolean = false,
    val showCircularIndication: Boolean = false,
)
