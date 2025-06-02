package ru.nyxsed.postscan.common.presentation.screens.postsscreen

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.common.domain.models.SettingKey
import ru.nyxsed.postscan.common.presentation.elements.CenteredLoadingIndicator
import ru.nyxsed.postscan.common.util.Constants.findOrFirst
import ru.nyxsed.postscan.common.util.Constants.mihonIntent
import ru.nyxsed.postscan.common.util.UiEvent
import ru.nyxsed.postscan.features.groups.presentation.GroupsScreen
import ru.nyxsed.postscan.features.imagepager.presentation.ImagePagerArgs
import ru.nyxsed.postscan.features.imagepager.presentation.ImagePagerScreen
import ru.nyxsed.postscan.features.preferences.presentation.PreferencesScreen
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
val PostsScreen by navDestination<Unit> {
    val postsScreenViewModel = koinViewModel<PostsScreenViewModel>()
    val postListState = postsScreenViewModel.posts.collectAsState()
    val groupListState = postsScreenViewModel.groups.collectAsState()

    val navController = navController()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = SnackbarHostState()
    val scope = rememberCoroutineScope()

    var groupSelected = postsScreenViewModel.groupSelected.collectAsState()
    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var settingUseMihon by remember { mutableStateOf(true) }
    var settingDeleteAfterLike by remember { mutableStateOf(false) }

    var showedTutorial by remember { mutableStateOf(true) }

    var showCircularIndicator by remember { mutableStateOf(false) }

    val sortOption by postsScreenViewModel.sortOption.collectAsState()


    LaunchedEffect(Unit) {
        settingUseMihon = postsScreenViewModel.getSettingBoolean(SettingKey.USE_MIHON)
        settingDeleteAfterLike = postsScreenViewModel.getSettingBoolean(SettingKey.DELETE_AFTER_LIKE)
        showedTutorial = postsScreenViewModel.getSettingBoolean(SettingKey.SHOWED_TUTORIAL_POSTS)

        postsScreenViewModel.uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                is UiEvent.Navigate ->
                    navController.navigate(event.destination)

                is UiEvent.NavigateToPost ->
                    navController.navigate(event.destination, event.navArgs)

                is UiEvent.Scroll ->
                    scrollState.scrollToItem(0)

                is UiEvent.UpdateStatus ->
                    showCircularIndicator = event.status

                else -> {}
            }
        }
    }
    sortOption?.let {
        Scaffold(
            topBar = {
                PostsScreenBar(
                    onRefreshClicked = {
                        postsScreenViewModel.refreshPosts(context)
                    },
                    onNavToGroupsClicked = {
                        navController.navigate(GroupsScreen)
                    },
                    onNavToSettingsClicked = {
                        navController.navigate(PreferencesScreen)
                    },
                    scrollBehavior = scrollBehavior,
                    showShowcase = !showedTutorial,
                    onShowcaseShowed = {
                        showedTutorial = true
                        postsScreenViewModel.setSettingBoolean(SettingKey.SHOWED_TUTORIAL_POSTS, true)
                    },
                    onSortClicked = {
                        if (sortOption == SortOption.ASCENDING) {
                            postsScreenViewModel.changeSorting(SortOption.DESCENDING)
                        } else {
                            postsScreenViewModel.changeSorting(SortOption.ASCENDING)
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
                        groupListState.value.forEach { group ->
                            val postCount = postListState.value.filter { it.ownerId.absoluteValue == group.groupId }.size
                            if (postCount > 0) {
                                item(
                                    key = group.groupId
                                ) {
                                    GroupChip(
                                        group = group,
                                        isSelected = group.groupId == groupSelected.value,
                                        postCount = postCount,
                                        onChipClicked = {
                                            if (groupSelected.value != group.groupId) {
                                                postsScreenViewModel.selectGroup(group.groupId)
                                            } else {
                                                postsScreenViewModel.selectGroup(0L)
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
                    if (postListState.value.isEmpty()) {
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
                            val sortedList = when (sortOption) {
                                SortOption.ASCENDING -> postListState.value.sortedBy { it.postId }
                                SortOption.DESCENDING -> postListState.value.sortedByDescending { it.postId }
                                null -> postListState.value.sortedBy { it.postId }
                            }
                            items(
                                items = sortedList.filter {
                                    if (groupSelected.value == 0L) true else it.ownerId.absoluteValue == groupSelected.value
                                },
                                key = { it.postId }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .animateItem()
                                ) {
                                    PostCard(
                                        post = it,
                                        settingUseMihon = settingUseMihon,
                                        onPostDeleteClicked = {
                                            postsScreenViewModel.deletePost(
                                                post = it,
                                                context = context,
                                                snackbarHostState = snackbarHostState
                                            )
                                        },
                                        onLikeClicked = {
                                            postsScreenViewModel.changeLikeStatus(
                                                post = it,
                                                settingDeleteAfterLike = settingDeleteAfterLike,
                                                context = context,
                                                snackbarHostState = snackbarHostState
                                            )
                                        },
                                        onToVkClicked = {
                                            postsScreenViewModel.openPostUri(
                                                uriHandler = uriHandler,
                                                post = it
                                            )
                                        },
                                        onToMihonClicked = {
                                            val intent = mihonIntent(
                                                query = it
                                            )
                                            context.startActivity(intent)
                                        },
                                        onTextLongClick = {
                                            clipboardManager.setText(
                                                annotatedString = AnnotatedString(it.contentText)
                                            )
                                        },
                                        onImageClicked = { content, index ->
                                            val imagePagerArgs = ImagePagerArgs(content, index)
                                            navController.navigate(ImagePagerScreen, imagePagerArgs)
                                        },
                                        onCommentsClicked = {
                                            postsScreenViewModel.toComments(it)
                                        },
                                        onGroupClicked = { post ->
                                            val group =
                                                groupListState.value.findOrFirst { it.groupId == post.ownerId.absoluteValue }
                                            postsScreenViewModel.openGroupUri(
                                                uriHandler = uriHandler,
                                                group = group
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                if (showCircularIndicator) {
                    CenteredLoadingIndicator()
                }
            }
        }
    }
}

