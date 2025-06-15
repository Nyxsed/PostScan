package ru.nyxsed.postscan.features.groups.presentation

import ru.nyxsed.postscan.core.domain.models.Group

sealed class GroupsIntent {
    object DeleteGroupWithPosts : GroupsIntent()
    object DeleteAllPosts: GroupsIntent()
    object GroupsTutorialCompleted : GroupsIntent()
    object ToggleAddDialog : GroupsIntent()
    object ToggleDownloadDialog : GroupsIntent()
    object ToggleDeleteAllDialog : GroupsIntent()
    data class ToggleDeleteDialog(val group: Group?) : GroupsIntent()
    data class NavigateToChangeGroupScreen(val group: Group) : GroupsIntent()
    data class NavigateToPickScreen(val dest: String): GroupsIntent()
    data class LoadPosts(val startDate: String, val endDate: String): GroupsIntent()
}