package ru.nyxsed.postscan.features.groups.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import org.koin.androidx.compose.koinViewModel
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.event.CollectUiEvent
import ru.nyxsed.postscan.uikit.components.AddModalDialog
import ru.nyxsed.postscan.uikit.components.CenteredLoadingIndicator
import ru.nyxsed.postscan.uikit.components.DeleteModalDialog
import ru.nyxsed.postscan.uikit.components.DownloadModalDialog
import ru.nyxsed.postscan.uikit.components.GroupCard

@OptIn(ExperimentalMaterial3Api::class)
val GroupsScreen by navDestination<Unit> {
    val navController = navController()
    val context = LocalContext.current

    val groupScreenViewModel = koinViewModel<GroupsScreenViewModel>()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val groupsState = groupScreenViewModel.dbGroups.collectAsState()
    val showAddDialog = groupScreenViewModel.showAddDialog.collectAsState()
    val showDeleteDialog = groupScreenViewModel.showDeleteDialog.collectAsState()
    val showDeleteAllDialog = groupScreenViewModel.showDeleteAllDialog.collectAsState()
    val showDownloadDialog = groupScreenViewModel.showDownloadDialog.collectAsState()

    var showedTutorial = groupScreenViewModel.showTutorial.collectAsState()

    var showCircularIndicator = groupScreenViewModel.showCircularIndicator.collectAsState()

    LaunchedEffect(Unit) {
        groupScreenViewModel.showTutorial()
    }

    CollectUiEvent(
        uiEventFlow = groupScreenViewModel.uiEventFlow,
        navController = navController,
    )

    GroupScreenContent(
        groupScreenViewModel = groupScreenViewModel,
        scrollBehavior = scrollBehavior,
        groupsState = groupsState,
        showAddDialog = showAddDialog,
        showDeleteDialog = showDeleteDialog,
        showDeleteAllDialog = showDeleteAllDialog,
        showDownloadDialog = showDownloadDialog,
        showedTutorial = showedTutorial,
        showCircularIndicator = showCircularIndicator,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreenContent(
    groupScreenViewModel: GroupsScreenViewModel,
    scrollBehavior: TopAppBarScrollBehavior,
    groupsState: State<List<Group>>,
    showAddDialog: State<Boolean>,
    showDeleteDialog: State<Boolean>,
    showDeleteAllDialog: State<Boolean>,
    showDownloadDialog: State<Boolean>,
    showedTutorial: State<Boolean>,
    showCircularIndicator: State<Boolean>,
) {
    IntroShowcase(
        showIntroShowCase = !showedTutorial.value,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            groupScreenViewModel.setSettingBoolean(SettingKey.SHOWED_TUTORIAL_GROUPS, true)
        }
    ) {
        Scaffold(
            floatingActionButton = {

                FloatingActionButton(
                    onClick = {
                        groupScreenViewModel.toggleAddDialog()
                    },
                    modifier = Modifier.introShowCaseTarget(
                        index = 0,
                        style = ShowcaseStyle.Default.copy(
                            backgroundColor = Color(0xFF1C0A00), // specify color of background
                            backgroundAlpha = 0.98f, // specify transparency of background
                            targetCircleColor = Color.White
                        ),
                        content = {
                            Column {
                                Text(
                                    text = stringResource(R.string.tutorial_add_group),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.tutorial_add_group_desc),
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
            },
            topBar = {
                GroupsScreenBar(
                    onDownloadClicked = {
                        groupScreenViewModel.toggleDownloadDialog()
                    },
                    onDeleteClicked = {
                        groupScreenViewModel.toggleDeleteAllDialog()
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddings ->
            Box(
                modifier = Modifier
                    .padding(paddings)
                    .fillMaxSize()
            ) {
                if (groupsState.value.isEmpty()) {
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
                        modifier = Modifier
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = groupsState.value,
                            key = { it.groupId }
                        ) {
                            Box(
                                modifier = Modifier
                                    .animateItem()
                            ) {
                                GroupCard(
                                    group = it,
                                    onGroupDeleteClicked = {
                                        groupScreenViewModel.toggleDeleteDialog(it)
                                    },
                                    onGroupClicked = {
                                        groupScreenViewModel.navigateToChangeGroupScreen(it)
                                    },
                                    deleteEnabled = true
                                )
                            }
                        }
                    }
                }
                if (showCircularIndicator.value) {
                    CenteredLoadingIndicator()
                }
            }
            AddModalDialog(
                showDialog = showAddDialog.value,
                onDismiss = {
                    groupScreenViewModel.toggleAddDialog()
                },
                onSearchClicked = {
                    groupScreenViewModel.navigateToPickScreen("SEARCH")
                },
                onPickClicked = {
                    groupScreenViewModel.navigateToPickScreen("USER_GROUPS")
                }
            )
            DeleteModalDialog(
                title = stringResource(R.string.delete_group),
                description = stringResource(R.string.group_delete_dialog_question),
                showDialog = showDeleteDialog.value,
                onDismiss = {
                    groupScreenViewModel.toggleDeleteDialog()
                },
                onConfirmClicked = {
                    groupScreenViewModel.deleteGroupWithPosts()
                }
            )
            DeleteModalDialog(
                title = stringResource(R.string.delete_all_posts),
                description = stringResource(R.string.delete_all_posts_dialog_question),
                showDialog = showDeleteAllDialog.value,
                onDismiss = {
                    groupScreenViewModel.toggleDeleteAllDialog()
                },
                onConfirmClicked = {
                    groupScreenViewModel.deleteAllPosts()
                }
            )
            DownloadModalDialog(
                showDialog = showDownloadDialog.value,
                onDismiss = {
                    groupScreenViewModel.toggleDownloadDialog()
                },
                onDownloadClicked = { startDate, endDate ->
                    groupScreenViewModel.loadPosts(
                        startDate = startDate,
                        endDate = endDate
                    )
                }
            )
        }
    }
}