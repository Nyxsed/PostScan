package ru.nyxsed.postscan.core.domain.models

data class ImagePagerArgs(
    val listContent: List<Content>,
    val index: Int,
)