package ru.nyxsed.postscan.features.posts.presentation

import com.composegears.tiamat.NavDestination
import ru.nyxsed.postscan.core.domain.models.Content
import ru.nyxsed.postscan.core.domain.models.Post

sealed class PostsIntent {
    data class OpenUri(val query: String) : PostsIntent()
    data class OpenMihon(val query: String) : PostsIntent()
    data class CopyToClipboard(val text: String) : PostsIntent()
    data class SelectGroup(val groupId: Long) : PostsIntent()
    data class NavigateToComments(val post: Post) : PostsIntent()
    data class DeletePost(val post: Post) : PostsIntent()
    data class ChangeLikeStatus(val post: Post) : PostsIntent()
    data class NavigateToImagePager(val list: List<Content>, val index: Int) : PostsIntent()
    data class ChangeSorting(val sortOption: SortOption) : PostsIntent()
    data class Navigate(val destination: NavDestination<Unit>) : PostsIntent()
    object RefreshPosts : PostsIntent()
    object ShowedTutorial : PostsIntent()
}