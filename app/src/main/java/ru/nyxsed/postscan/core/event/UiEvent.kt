package ru.nyxsed.postscan.core.event

import com.composegears.tiamat.NavDestination

/**
 * UI-события, отправляемые из ViewModel и не включаемые в State.
 *
 * Используются для однократных действий: тосты, навигация, скролл,
 * работа с буфером обмена, открытие ссылок и т. д.
 */
sealed class UiEvent {
    class ShowToast(val message: String) : UiEvent()
    class OpenUrl(val url: String) : UiEvent()
    class NavigateBack() : UiEvent()
    class NavigateTo<T>(val destination: NavDestination<T>, val navArgs: T? = null) : UiEvent()
    class Scroll() : UiEvent()
    class OpenMihon(val query: String) : UiEvent()
    class CopyToClipboard(val text: String) : UiEvent()
    class ShowSnackbar(val messageID: Int, val actionLabelID: Int, val onAction:() -> Unit) : UiEvent()
}