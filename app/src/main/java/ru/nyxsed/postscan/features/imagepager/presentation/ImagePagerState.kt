package ru.nyxsed.postscan.features.imagepager.presentation

import ru.nyxsed.postscan.core.domain.models.Content

data class ImagePagerState(
    val fullScreen : Boolean = false,
    val showTutorial : Boolean = false,
    val expendedMenu : Boolean = false,
    val contentList: List<Content> = emptyList(),
    val pageIndex : Int = 0,
    val pageData: Map<Int, Boolean> = emptyMap(),
)
