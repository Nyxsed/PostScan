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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.composegears.tiamat.navArgs
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import org.koin.androidx.compose.koinViewModel
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.event.CollectUiEvent
import ru.nyxsed.postscan.core.util.Constants.toStringDate
import ru.nyxsed.postscan.uikit.components.BasicButton
import ru.nyxsed.postscan.uikit.components.CenteredLoadingIndicator
import ru.nyxsed.postscan.uikit.components.DatePickerTextField
import ru.nyxsed.postscan.uikit.components.DeleteModalDialog
import ru.nyxsed.postscan.uikit.components.DownloadModalDialog


val ChangeGroupScreen by navDestination<Group> {
    val group = navArgs()
    val changeGroupViewModel = koinViewModel<ChangeGroupViewModel>()
    val navController = navController()
    val uriHandler = LocalUriHandler.current

    val groupId = changeGroupViewModel.groupId.collectAsState()
    var groupName = changeGroupViewModel.groupName.collectAsState()
    var screenName = changeGroupViewModel.screenName.collectAsState()
    var avatarUrl = changeGroupViewModel.avatarUrl.collectAsState()
    var lastFetchDate = changeGroupViewModel.lastFetchDate.collectAsState()

    val showDeleteDialog = changeGroupViewModel.showDeleteDialog.collectAsState()
    val showDownloadDialog = changeGroupViewModel.showDownloadDialog.collectAsState()

    var showCircularIndicator = changeGroupViewModel.showCircularIndicator.collectAsState()

    LaunchedEffect(Unit) {
        group.let {
            changeGroupViewModel.changeGroupId(it.groupId)
            changeGroupViewModel.changeGroupName(it.name)
            changeGroupViewModel.changeScreenName(it.screenName)
            changeGroupViewModel.changeAvatarUrl(it.avatarUrl)
            changeGroupViewModel.changeLastFetchDate(it.lastFetchDate.toStringDate().replace(".", ""))
        }
    }

    CollectUiEvent(
        uiEventFlow = changeGroupViewModel.uiEventFlow,
        navController = navController,
        uriHandler = uriHandler,
    )

    ChangeGroupScreenContent(
        group = group,
        changeGroupViewModel = changeGroupViewModel,
        groupId = groupId,
        groupName = groupName,
        screenName = screenName,
        avatarUrl = avatarUrl,
        lastFetchDate = lastFetchDate,
        showDownloadDialog = showDownloadDialog,
        showDeleteDialog = showDeleteDialog,
        showCircularIndicator = showCircularIndicator,
    )
}

@Composable
fun ChangeGroupScreenContent(
    group: Group,
    changeGroupViewModel: ChangeGroupViewModel,
    groupId: State<Long>,
    groupName: State<String>,
    screenName: State<String>,
    avatarUrl: State<String>,
    lastFetchDate: State<String>,
    showDeleteDialog: State<Boolean>,
    showDownloadDialog: State<Boolean>,
    showCircularIndicator: State<Boolean>,
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
                                changeGroupViewModel.openGroupUri(group)
                            }
                        ),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl.value)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    value = groupName.value,
                    onValueChange = {
                        changeGroupViewModel.changeGroupName(it)
                    },
                    label = {
                        Text(stringResource(R.string.group_name))
                    }
                )
                DatePickerTextField(
                    label = stringResource(R.string.last_fetch_date),
                    selectedDate = lastFetchDate.value,
                    onDateSelected = { newDate ->
                        changeGroupViewModel.changeLastFetchDate(newDate)
                    }
                )
                BasicButton(
                    label = stringResource(R.string.update_group),
                    onClick = {
                        changeGroupViewModel.updateGroup(
                            groupId.value,
                            groupName.value,
                            screenName.value,
                            avatarUrl.value,
                            lastFetchDate.value
                        )
                    },
                    enabled = changeGroupViewModel.regex.matches(lastFetchDate.value) && groupName.value.isNotEmpty()
                )
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
                BasicButton(
                    label = stringResource(R.string.download_posts),
                    onClick = { changeGroupViewModel.toggleDownloadDialog() }
                )
                BasicButton(
                    label = stringResource(R.string.delete_posts),
                    onClick = { changeGroupViewModel.toggleDeleteDialog() }
                )
            }
            if (showCircularIndicator.value) {
                CenteredLoadingIndicator()
            }
        }
        DownloadModalDialog(
            showDialog = showDownloadDialog.value,
            onDismiss = {
                changeGroupViewModel.toggleDownloadDialog()
            },
            onDownloadClicked = { startDate, endDate ->
                changeGroupViewModel.loadPosts(
                    group = group,
                    startDate = startDate,
                    endDate = endDate
                )
            }
        )
        DeleteModalDialog(
            title = stringResource(R.string.delete_posts),
            description = stringResource(R.string.do_you_want_to_delete_all_posts_for_this_group),
            showDialog = showDeleteDialog.value,
            onDismiss = {
                changeGroupViewModel.toggleDeleteDialog()
            },
            onConfirmClicked = {
                changeGroupViewModel.deleteGroupWithPosts(group)
            }
        )
    }
}