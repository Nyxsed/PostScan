package ru.nyxsed.postscan.features.posts.presentation

import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.Post

data class PostsState(
    val posts : List<Post> = emptyList(),
    val groups : List<Group> = emptyList(),
    val sortOption: SortOption = SortOption.ASCENDING,
    val selectedGroupId : Long = 0L,
    val settingUseMihon: Boolean = false,
    val settingDeleteAfterLike: Boolean = false,
    val showedTutorial: Boolean = false,
    val showCircularIndicator: Boolean = false
)
