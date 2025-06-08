package ru.nyxsed.postscan.features.changegroup.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.composegears.tiamat.navArgs
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.event.CollectUiEvent
import ru.nyxsed.postscan.uikit.components.BasicButton
import ru.nyxsed.postscan.uikit.components.CenteredLoadingIndicator
import ru.nyxsed.postscan.uikit.components.DatePickerTextField
import ru.nyxsed.postscan.uikit.components.DeleteModalDialog
import ru.nyxsed.postscan.uikit.components.DownloadModalDialog


val ChangeGroupScreen by navDestination<Group> {
    val group = navArgs()
    val changeGroupViewModel: ChangeGroupViewModel = koinViewModel(
        parameters = { parametersOf(group) }
    )
    val navController = navController()
    val state by changeGroupViewModel.state.collectAsState()

    CollectUiEvent(
        uiEventFlow = changeGroupViewModel.uiEventFlow,
        navController = navController,
    )

    ChangeGroupScreenContent(
        state = state,
        processIntent = {
            changeGroupViewModel.processIntent(it)
        }
    )
}

@Composable
fun ChangeGroupScreenContent(
    state: ChangeGroupState,
    processIntent: (ChangeGroupIntent) -> Unit,
) {
    Scaffold { paddings ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(50.dp)
                        .clickable(
                            onClick = {
                                processIntent(ChangeGroupIntent.OpenGroupUri)
                            }
                        ),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(state.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    value = state.groupName,
                    onValueChange = {
                        processIntent(ChangeGroupIntent.ChangeGroupName(it))
                    },
                    label = {
                        Text(stringResource(R.string.group_name))
                    }
                )
                DatePickerTextField(
                    label = stringResource(R.string.last_fetch_date),
                    selectedDate = state.lastFetchDate,
                    onDateSelected = { newDate -> processIntent(ChangeGroupIntent.ChangeLastFetchDate(newDate)) }
                )
                BasicButton(
                    label = stringResource(R.string.update_group),
                    onClick = {
                        processIntent(ChangeGroupIntent.UpdateGroup)
                    },
                    enabled = Regex("^([0-2][0-9]|3[01])(0[1-9]|1[0-2])[0-9]{4}$").matches(state.lastFetchDate) && state.groupName.isNotEmpty()
                )
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
                BasicButton(
                    label = stringResource(R.string.download_posts),
                    onClick = { processIntent(ChangeGroupIntent.ToggleDownloadDialog) }
                )
                BasicButton(
                    label = stringResource(R.string.delete_posts),
                    onClick = { processIntent(ChangeGroupIntent.ToggleDeleteDialog) }
                )
            }
            if (state.showCircularIndicator) {
                CenteredLoadingIndicator()
            }
        }
        DownloadModalDialog(
            showDialog = state.showDownloadDialog,
            onDismiss = { processIntent(ChangeGroupIntent.ToggleDownloadDialog) },
            onDownloadClicked = { startDate, endDate -> processIntent(ChangeGroupIntent.LoadPosts(startDate, endDate)) }
        )
        DeleteModalDialog(
            title = stringResource(R.string.delete_posts),
            description = stringResource(R.string.do_you_want_to_delete_all_posts_for_this_group),
            showDialog = state.showDeleteDialog,
            onDismiss = { processIntent(ChangeGroupIntent.ToggleDeleteDialog) },
            onConfirmClicked = { processIntent(ChangeGroupIntent.DeleteGroupPosts) }
        )
    }
}