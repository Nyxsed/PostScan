package ru.nyxsed.postscan.features.imagepager.presentation

import ru.nyxsed.postscan.core.domain.models.Content

sealed class ImagePagerIntent {
    object ToggleFullScreen : ImagePagerIntent()
    object ToggleMenu : ImagePagerIntent()
    object ImageTutorialCompleted : ImagePagerIntent()
    object NavigateBack : ImagePagerIntent()
    class FindImage(val link: String, val source: String) :  ImagePagerIntent()
    class OpenPostUri(val content: Content) :  ImagePagerIntent()
    class PageChanged(val pageIndex: Int) : ImagePagerIntent()
    class LikeClicked(val pageIndex: Int) : ImagePagerIntent()
}