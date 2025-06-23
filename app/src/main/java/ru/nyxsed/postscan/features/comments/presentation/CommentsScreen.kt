package ru.nyxsed.postscan.features.comments.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.event.CollectUiEvent

/**
 * Экран комментариев к посту
 *
 */
val CommentsScreen by navDestination<Post> {
    val args = navArgs()
    val commentsViewModel = koinViewModel<CommentsViewModel>(
        key = args.postId.toString(),
        parameters = { parametersOf(args) }
    )
    val state by commentsViewModel.state.collectAsState()
    val navController = navController()

    CollectUiEvent(
        uiEventFlow = commentsViewModel.uiEventFlow,
        navController = navController,
    )

    CommentsScreenContent(
        state = state,
        processIntent = {
            commentsViewModel.processIntent(it)
        }
    )
}

@Composable
fun CommentsScreenContent(
    state: CommentsState,
    processIntent: (CommentsIntent) -> Unit,
) {
    Scaffold { paddings ->
        if (state.comments.isEmpty()) {
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
                modifier = Modifier
                    .padding(paddings),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = state.comments
                        .filter { it.parentStack == null }
                        .filter { it.contentText.isNotEmpty() || it.content.isNotEmpty() },
                    key = { it.commentId }
                ) { originalComment ->
                    CommentCard(
                        comment = originalComment,
                        replays = state.comments
                            .filter { it.parentStack == originalComment.commentId }
                            .filter { it.contentText.isNotEmpty() || it.content.isNotEmpty() },
                        settingUseMihon = state.settingMihon,
                        onToMihonClicked = {
                            processIntent(CommentsIntent.OnMihonClicked(it.contentText))
                        },
                        onTextLongClick = {
                            processIntent(CommentsIntent.OnTextLongClick(it))
                        },
                        onImageClicked = { content, index ->
                            processIntent(CommentsIntent.OnImageClicked(content, index))
                        }
                    )
                }
            }
        }
    }
}