package ru.nyxsed.postscan.features.preferences.presentation

data class PreferencesState(
    val notLoadLikedPosts: Boolean = false,
    val useMihon: Boolean = false,
    val deleteAfterLike: Boolean = false,
)