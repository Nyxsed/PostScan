package ru.nyxsed.postscan.features.pickgroup.presentation

import ru.nyxsed.postscan.core.domain.models.Group

sealed class PickGroupIntent {
    class ChangeSearchQuery(val query : String) : PickGroupIntent()
    class FetchGroups(val query : String) : PickGroupIntent()
    class ToggleDeleteDialog(val group: Group?) : PickGroupIntent()
    class AddGroup(val group: Group) : PickGroupIntent()
    object NavigateBack : PickGroupIntent()
    object DeleteGroupWithPosts : PickGroupIntent()
}