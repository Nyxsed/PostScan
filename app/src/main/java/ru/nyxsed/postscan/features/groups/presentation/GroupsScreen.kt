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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import ru.nyxsed.postscan.core.domain.models.PickGroupMode
import ru.nyxsed.postscan.core.event.CollectUiEvent
import ru.nyxsed.postscan.uikit.components.AddModalDialog
import ru.nyxsed.postscan.uikit.components.CenteredLoadingIndicator
import ru.nyxsed.postscan.uikit.components.DeleteModalDialog
import ru.nyxsed.postscan.uikit.components.DownloadModalDialog
import ru.nyxsed.postscan.uikit.components.GroupCard

/**
 * Экран списка групп
 */
@OptIn(ExperimentalMaterial3Api::class)
val GroupsScreen by navDestination<Unit> {
    val navController = navController()
    val groupsViewModel = koinViewModel<GroupsViewModel>()

    val state by groupsViewModel.state.collectAsState()

    CollectUiEvent(
        uiEventFlow = groupsViewModel.uiEventFlow,
        navController = navController,
    )

    GroupScreenContent(
        state = state,
        processIntent = {
            groupsViewModel.processIntent(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreenContent(
    state: GroupsState,
    processIntent: (GroupsIntent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    IntroShowcase(
        showIntroShowCase = !state.showTutorial,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            processIntent(GroupsIntent.GroupsTutorialCompleted)
        }
    ) {
        Scaffold(
            floatingActionButton = {

                FloatingActionButton(
                    onClick = {
                        processIntent(GroupsIntent.ToggleAddDialog)
                    },
                    modifier = Modifier.introShowCaseTarget(
                        index = 0,
                        style = ShowcaseStyle.Default.copy(
                            backgroundColor = Color(0xFF1C0A00),
                            backgroundAlpha = 0.98f,
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
                        processIntent(GroupsIntent.ToggleDownloadDialog)
                    },
                    onDeleteClicked = {
                        processIntent(GroupsIntent.ToggleDeleteAllDialog)
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
                if (state.groups.isEmpty()) {
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
                            items = state.groups,
                            key = { it.groupId }
                        ) {
                            Box(
                                modifier = Modifier
                                    .animateItem()
                            ) {
                                GroupCard(
                                    group = it,
                                    onGroupDeleteClicked = {
                                        processIntent(GroupsIntent.ToggleDeleteDialog(it))
                                    },
                                    onGroupClicked = {
                                        processIntent(GroupsIntent.NavigateToChangeGroupScreen(it))
                                    },
                                    deleteEnabled = true
                                )
                            }
                        }
                    }
                }
                if (state.showCircularIndication) {
                    CenteredLoadingIndicator()
                }
            }
            AddModalDialog(
                showDialog = state.showAddDialog,
                onDismiss = {
                    processIntent(GroupsIntent.ToggleAddDialog)
                },
                onSearchClicked = {
                    processIntent(GroupsIntent.NavigateToPickScreen(PickGroupMode.SEARCH))
                },
                onPickClicked = {
                    processIntent(GroupsIntent.NavigateToPickScreen(PickGroupMode.USER))
                }
            )
            DeleteModalDialog(
                title = stringResource(R.string.delete_group),
                description = stringResource(R.string.group_delete_dialog_question),
                showDialog = state.showDeleteDialog,
                onDismiss = {
                    processIntent(GroupsIntent.ToggleDeleteDialog(null))
                },
                onConfirmClicked = {
                    processIntent(GroupsIntent.DeleteGroupWithPosts)
                }
            )
            DeleteModalDialog(
                title = stringResource(R.string.delete_all_posts),
                description = stringResource(R.string.delete_all_posts_dialog_question),
                showDialog = state.showDeleteAllDialog,
                onDismiss = {
                    processIntent(GroupsIntent.ToggleDeleteAllDialog)
                },
                onConfirmClicked = {
                    processIntent(GroupsIntent.DeleteAllPosts)
                }
            )
            DownloadModalDialog(
                showDialog = state.showDownloadDialog,
                onDismiss = {
                    processIntent(GroupsIntent.ToggleDownloadDialog)
                },
                onDownloadClicked = { startDate, endDate ->
                    processIntent(GroupsIntent.LoadPosts(startDate, endDate))
                }
            )
        }
    }
}