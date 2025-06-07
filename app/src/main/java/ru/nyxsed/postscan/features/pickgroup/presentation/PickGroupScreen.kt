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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composegears.tiamat.navArgs
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import org.koin.androidx.compose.koinViewModel
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.event.CollectUiEvent
import ru.nyxsed.postscan.uikit.components.DeleteModalDialog
import ru.nyxsed.postscan.uikit.components.GroupCard

val PickGroupScreen by navDestination<String> {
    val mode = navArgs()
    val context = LocalContext.current
    val navController = navController()

    val pickGroupViewModel = koinViewModel<PickGroupViewModel>()
    val screenState = pickGroupViewModel.screenStateFlow.collectAsState()

    var searchQuery = pickGroupViewModel.searchQuery.collectAsState()
    val showDeleteDialog = pickGroupViewModel.showDeleteDialog.collectAsState()

    LaunchedEffect(mode) {
        pickGroupViewModel.setMode(mode)
    }

    CollectUiEvent(
        uiEventFlow = pickGroupViewModel.uiEventFlow,
        navController = navController
    )

    PickGroupContent(
        pickGroupViewModel = pickGroupViewModel,
        screenState = screenState,
        searchQuery = searchQuery,
        showDeleteDialog = showDeleteDialog
    )
}

@Composable
fun PickGroupContent(
    pickGroupViewModel: PickGroupViewModel,
    screenState: State<PickGroupState>,
    searchQuery: State<String>,
    showDeleteDialog: State<Boolean>,
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
                val currentState = screenState.value

                when (currentState) {
                    is PickGroupState.Loading -> {
                        SearchView(
                            searchQuery = searchQuery,
                            onSearchQueryChange = {
                                pickGroupViewModel.changeSearchQuery(it)
                            },
                            onSearchClicked = {
                                pickGroupViewModel.fetchedGroups(it)
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

                    is PickGroupState.Search -> {
                        SearchView(
                            onSearchClicked = {
                                pickGroupViewModel.fetchedGroups(it)
                            },
                            searchQuery = searchQuery,
                            onSearchQueryChange = {
                                pickGroupViewModel.changeSearchQuery(it)
                            },
                        )
                        GroupsLazyColum(
                            groupState = screenState,
                            onGroupCardClicked = {
                                val existingGroup = currentState.existingGroups.any { existed ->
                                    existed.groupId == it.groupId
                                }
                                if (existingGroup) {
                                    pickGroupViewModel.toggleDeleteDialog(it)
                                } else {
                                    pickGroupViewModel.addGroup(it)
                                }
                            }
                        )
                    }

                    is PickGroupState.User -> {
                        GroupsLazyColum(
                            groupState = screenState,
                            onGroupCardClicked = {
                                val existingGroup = currentState.existingGroups.any { existed ->
                                    existed.groupId == it.groupId
                                }
                                if (existingGroup) {
                                    pickGroupViewModel.toggleDeleteDialog(it)
                                } else {
                                    pickGroupViewModel.addGroup(it)
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
                    pickGroupViewModel.navigateBack()
                },
            ) {
                Text(text = stringResource(R.string.back))
            }
        }
        DeleteModalDialog(
            title = stringResource(R.string.delete_group),
            description = stringResource(R.string.group_delete_dialog_question),
            showDialog = showDeleteDialog.value,
            onDismiss = {
                pickGroupViewModel.toggleDeleteDialog()
            },
            onConfirmClicked = {
                pickGroupViewModel.deleteGroupWithPosts()
            }
        )
    }
}

@Composable
fun SearchView(
    searchQuery: State<String>,
    onSearchQueryChange: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        value = searchQuery.value,
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
            onSearchClicked(searchQuery.value)
        }
    ) {
        Text(stringResource(R.string.search_for_group))
    }
}

@Composable
fun GroupsLazyColum(
    groupState: State<PickGroupState>,
    onGroupCardClicked: (Group) -> Unit,
) {
    val existingGroups = when (val state = groupState.value) {
        is PickGroupState.Search -> state.existingGroups
        is PickGroupState.User -> state.existingGroups
        is PickGroupState.Loading -> emptyList()
    }

    val fetchedGroups = when (val state = groupState.value) {
        is PickGroupState.Search -> state.groups
        is PickGroupState.User -> state.groups
        is PickGroupState.Loading -> emptyList()
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
