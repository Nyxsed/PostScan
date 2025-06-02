package ru.nyxsed.postscan.core.util

import com.composegears.tiamat.NavDestination
import ru.nyxsed.postscan.core.domain.models.entity.Group
import ru.nyxsed.postscan.core.domain.models.entity.Post

sealed class UiEvent {
    class ShowToast(val message: String) : UiEvent()
    class OpenUrl(val url: String) : UiEvent()
    class Navigate(val destination: NavDestination<Unit>) : UiEvent()
    class NavigateBack() : UiEvent()
    class NavigateToPost(val destination: NavDestination<Post>, val navArgs: Post) : UiEvent()
    class NavigateToPicker(val destination: NavDestination<String>, val navArgs: String) : UiEvent()
    class NavigateToChangeGroup(val destination: NavDestination<Group>, val navArgs: Group) : UiEvent()
    class Scroll() : UiEvent()
    class UpdateStatus(val status: Boolean) : UiEvent()
}