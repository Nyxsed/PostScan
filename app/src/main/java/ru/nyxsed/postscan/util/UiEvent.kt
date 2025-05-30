package ru.nyxsed.postscan.util

import com.composegears.tiamat.NavDestination
import ru.nyxsed.postscan.common.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.common.domain.models.entity.PostEntity

sealed class UiEvent {
    class ShowToast(val message: String) : UiEvent()
    class OpenUrl(val url: String) : UiEvent()
    class Navigate(val destination: NavDestination<Unit>) : UiEvent()
    class NavigateBack() : UiEvent()
    class NavigateToPost(val destination: NavDestination<PostEntity>, val navArgs: PostEntity) : UiEvent()
    class NavigateToPicker(val destination: NavDestination<String>, val navArgs: String) : UiEvent()
    class NavigateToChangeGroup(val destination: NavDestination<GroupEntity>, val navArgs: GroupEntity) : UiEvent()
    class Scroll() : UiEvent()
    class InitNotification() : UiEvent()
    class ErrorNotification(val message: String) : UiEvent()
    class UpdateNotification(val percent: Int) : UiEvent()
    class CompleteNotification() : UiEvent()
    class UpdateStatus(val status: Boolean) : UiEvent()
}