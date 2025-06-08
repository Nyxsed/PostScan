package ru.nyxsed.postscan.core.event

import com.composegears.tiamat.NavDestination

sealed class UiEvent {
    class ShowToast(val message: String) : UiEvent()
    class OpenUrl(val url: String) : UiEvent()
    class NavigateBack() : UiEvent()
    class NavigateTo<T>(val destination: NavDestination<T>, val navArgs: T? = null) : UiEvent()
    class Scroll() : UiEvent()
    class UpdateStatus(val status: Boolean) : UiEvent()
    class OpenMihon(val query: String) : UiEvent()
    class CopyToClipboard(val text: String) : UiEvent()
}