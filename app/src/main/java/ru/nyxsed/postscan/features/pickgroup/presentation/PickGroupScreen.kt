package ru.nyxsed.postscan.features.pickgroup.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composegears.tiamat.navArgs
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.PickGroupMode
import ru.nyxsed.postscan.core.event.CollectUiEvent
import ru.nyxsed.postscan.uikit.components.DeleteModalDialog
import ru.nyxsed.postscan.uikit.components.GroupCard

val PickGroupScreen by navDestination<PickGroupMode> {
    val mode = navArgs()
    val navController = navController()

    val pickGroupViewModel = koinViewModel<PickGroupViewModel>(
        parameters = { parametersOf(mode) },
        key = mode.toString()
    )
    val state by pickGroupViewModel.state.collectAsState()

    CollectUiEvent(
        uiEventFlow = pickGroupViewModel.uiEventFlow,
        navController = navController
    )

    PickGroupContent(
        state = state,
        processIntent = {
            pickGroupViewModel.processIntent(it)
        }
    )
}

@Composable
fun PickGroupContent(
    state: PickGroupState,
    processIntent: (PickGroupIntent) -> Unit,
) {
    Scaffold { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                when (state.mode) {
                    PickGroupMode.LOADING -> {
                        SearchView(
                            searchQuery = state.searchQuery,
                            onSearchQueryChange = {
                                processIntent(PickGroupIntent.ChangeSearchQuery(it))
                            },
                            onSearchClicked = {
                                processIntent(PickGroupIntent.FetchGroups(it))
                            }
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    PickGroupMode.USER -> {
                        GroupsLazyColumn(
                            state = state,
                            onGroupCardClicked = {
                                val existingGroup = state.existingGroups.any { existed ->
                                    existed.groupId == it.groupId
                                }
                                if (existingGroup) {
                                    processIntent(PickGroupIntent.ToggleDeleteDialog(it))
                                } else {
                                    processIntent(PickGroupIntent.AddGroup(it))
                                }
                            }
                        )
                    }

                    PickGroupMode.SEARCH -> {
                        SearchView(
                            onSearchClicked = {
                                processIntent(PickGroupIntent.FetchGroups(it))
                            },
                            searchQuery = state.searchQuery,
                            onSearchQueryChange = {
                                processIntent(PickGroupIntent.ChangeSearchQuery(it))
                            },
                        )
                        GroupsLazyColumn(
                            state = state,
                            onGroupCardClicked = {
                                val existingGroup = state.existingGroups.any { existed ->
                                    existed.groupId == it.groupId
                                }
                                if (existingGroup) {
                                    processIntent(PickGroupIntent.ToggleDeleteDialog(it))
                                } else {
                                    processIntent(PickGroupIntent.AddGroup(it))
                                }
                            }
                        )
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                onClick = {
                    processIntent(PickGroupIntent.NavigateBack)
                },
            ) {
                Text(text = stringResource(R.string.back))
            }
        }
        DeleteModalDialog(
            title = stringResource(R.string.delete_group),
            description = stringResource(R.string.group_delete_dialog_question),
            showDialog = state.showDeleteDialog,
            onDismiss = {
                processIntent(PickGroupIntent.ToggleDeleteDialog(null))
            },
            onConfirmClicked = {
                processIntent(PickGroupIntent.DeleteGroupWithPosts)
            }
        )
    }
}

@Composable
fun SearchView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        value = searchQuery,
        onValueChange = {
            onSearchQueryChange(it)
        },
        label = {
            Text(stringResource(R.string.search_query))
        }
    )
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        onClick = {
            onSearchClicked(searchQuery)
        }
    ) {
        Text(stringResource(R.string.search_for_group))
    }
}

@Composable
fun GroupsLazyColumn(
    state: PickGroupState,
    onGroupCardClicked: (Group) -> Unit,
) {
    val existingGroups = when (state.mode) {
        PickGroupMode.LOADING -> emptyList()
        PickGroupMode.USER -> state.existingGroups
        PickGroupMode.SEARCH -> state.existingGroups
    }

    val fetchedGroups = when (state.mode) {
        PickGroupMode.LOADING -> emptyList()
        PickGroupMode.USER -> state.fetchedGroups
        PickGroupMode.SEARCH -> state.fetchedGroups
    }

    if (fetchedGroups.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_data_found),
                fontSize = 20.sp,
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = fetchedGroups,
                key = { it.groupId }
            ) {
                Box(
                    modifier = Modifier
                        .animateItem()
                ) {
                    GroupCard(
                        group = it,
                        deleteEnabled = false,
                        existingGroup = existingGroups.any { existed ->
                            existed.groupId == it.groupId
                        },
                        onGroupDeleteClicked = { },
                        onGroupClicked = {
                            onGroupCardClicked(it)
                        },
                    )
                }
            }
        }
    }
}
