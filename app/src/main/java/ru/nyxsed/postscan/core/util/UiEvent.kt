package ru.nyxsed.postscan.core.util

import com.composegears.tiamat.NavDestination
import ru.nyxsed.postscan.core.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.core.domain.models.entity.PostEntity

sealed class UiEvent {
    class ShowToast(val message: String) : UiEvent()
    class OpenUrl(val url: String) : UiEvent()
    class Navigate(val destination: NavDestination<Unit>) : UiEvent()
    class NavigateBack() : UiEvent()
    class NavigateToPost(val destination: NavDestination<PostEntity>, val navArgs: PostEntity) : UiEvent()
    class NavigateToPicker(val destination: NavDestination<String>, val navArgs: String) : UiEvent()
    class NavigateToChangeGroup(val destination: NavDestination<GroupEntity>, val navArgs: GroupEntity) : UiEvent()
    class Scroll() : UiEvent()
    class UpdateStatus(val status: Boolean) : UiEvent()
}