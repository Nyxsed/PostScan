package ru.nyxsed.postscan.features.posts.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.event.CollectUiEvent
import ru.nyxsed.postscan.core.util.Constants.VK_URL
import ru.nyxsed.postscan.core.util.Constants.VK_WALL_URL
import ru.nyxsed.postscan.core.util.Constants.findOrFirst
import ru.nyxsed.postscan.features.groups.presentation.GroupsScreen
import ru.nyxsed.postscan.features.preferences.presentation.PreferencesScreen
import ru.nyxsed.postscan.uikit.components.CenteredLoadingIndicator
import kotlin.math.absoluteValue

val PostsScreen by navDestination<Unit> {
    val postsViewModel = koinViewModel<PostsViewModel>()
    val navController = navController()
    val state by postsViewModel.state.collectAsState()

    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    val snackbarHostState = remember { SnackbarHostState() }

    CollectUiEvent(
        uiEventFlow = postsViewModel.uiEventFlow,
        navController = navController,
        scrollState = scrollState,
        snackbarHostState = snackbarHostState,
    )

    PostsScreenContent(
        state = state,
        processIntent = {
            postsViewModel.processIntent(it)
        },
        scrollState = scrollState,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreenContent(
    state: PostsState,
    processIntent: (PostsIntent) -> Unit,
    scrollState: LazyListState,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            PostsScreenBar(
                onRefreshClicked = {
                    processIntent(PostsIntent.RefreshPosts)
                },
                onNavToGroupsClicked = {
                    processIntent(PostsIntent.Navigate(GroupsScreen))
                },
                onNavToSettingsClicked = {
                    processIntent(PostsIntent.Navigate(PreferencesScreen))
                },
                scrollBehavior = scrollBehavior,
                showShowcase = !state.showedTutorial,
                onShowcaseShowed = {
                    processIntent(PostsIntent.ShowedTutorial)
                },
                onSortClicked = {
                    if (state.sortOption == SortOption.ASCENDING) {
                        processIntent(PostsIntent.ChangeSorting(SortOption.DESCENDING))
                    } else {
                        processIntent(PostsIntent.ChangeSorting(SortOption.ASCENDING))
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddings ->
        Box(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                // Group Chips
                LazyRow(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .zIndex(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    state.groups.forEach { group ->
                        val postCount = state.posts.filter { it.ownerId.absoluteValue == group.groupId }.size
                        if (postCount > 0) {
                            item(
                                key = group.groupId
                            ) {
                                GroupChip(
                                    group = group,
                                    isSelected = group.groupId == state.selectedGroupId,
                                    postCount = postCount,
                                    onChipClicked = {
                                        if (state.selectedGroupId != group.groupId) {
                                            processIntent(PostsIntent.SelectGroup(group.groupId))
                                        } else {
                                            processIntent(PostsIntent.SelectGroup(0L))
                                        }
                                        scope.launch {
                                            scrollState.scrollToItem(0)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                // Posts
                if (state.posts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_data_found),
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .padding(4.dp)
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item {
                            Spacer(
                                modifier = Modifier
                                    .height(37.dp),
                            )
                        }
                        val sortedList = when (state.sortOption) {
                            SortOption.ASCENDING -> state.posts.sortedBy { it.postId }
                            SortOption.DESCENDING -> state.posts.sortedByDescending { it.postId }
                        }
                        items(
                            items = sortedList.filter {
                                if (state.selectedGroupId == 0L) true else it.ownerId.absoluteValue == state.selectedGroupId
                            },
                            key = { it.postId }
                        ) {
                            Box(
                                modifier = Modifier
                                    .animateItem()
                            ) {
                                PostCard(
                                    post = it,
                                    settingUseMihon = state.settingUseMihon,
                                    onPostDeleteClicked = {
                                        processIntent(PostsIntent.DeletePost(it))
                                    },
                                    onLikeClicked = {
                                        processIntent(PostsIntent.ChangeLikeStatus(it))
                                    },
                                    onToVkClicked = {
                                        processIntent(PostsIntent.OpenUri("${VK_WALL_URL}${it.ownerId}_${it.postId}"))
                                    },
                                    onToMihonClicked = {
                                        processIntent(PostsIntent.OpenMihon(it))
                                    },
                                    onTextLongClick = {
                                        processIntent(PostsIntent.CopyToClipboard(it.contentText))
                                    },
                                    onImageClicked = { content, index ->
                                        processIntent(PostsIntent.NavigateToImagePager(content, index))
                                    },
                                    onCommentsClicked = {
                                        processIntent(PostsIntent.NavigateToComments(it))
                                    },
                                    onGroupClicked = { post ->
                                        val group = state.groups.findOrFirst { it.groupId == post.ownerId.absoluteValue }
                                        processIntent(PostsIntent.OpenUri("${VK_URL}${group.screenName}"))
                                    }
                                )
                            }
                        }
                    }
                }
            }
            if (state.showCircularIndicator) {
                CenteredLoadingIndicator()
            }
        }
    }
}