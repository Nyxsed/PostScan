package ru.nyxsed.postscan.presentation.screens.postsscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import org.koin.androidx.compose.koinViewModel
import ru.nyxsed.postscan.presentation.PostsScreenViewModel
import ru.nyxsed.postscan.presentation.screens.groupsscreen.GroupsScreen

val PostScreen by navDestination<Unit> {
    val postsScreenViewModel = koinViewModel<PostsScreenViewModel>()
    val postListState = postsScreenViewModel.posts.collectAsState()
    val navController = navController()
    Scaffold(
        topBar = {
            PostsScreenBar(
                onRefreshClicked = {
                    postsScreenViewModel.loadPosts()
                },
                onNavToGroupsClicked = {
                    navController.navigate(GroupsScreen)
                }
            )
        }
    ) { paddings ->
        LazyColumn(
            modifier = Modifier
                .padding(paddings),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            postListState.value.forEach {
                item {
                    PostCard(
                        post = it,
                        onPostDeleteClicked = {
                            postsScreenViewModel.deletePost(it)
                        }
                    )
                }
            }
        }
    }
}