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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composegears.tiamat.navArgs
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.models.entity.Post
import ru.nyxsed.postscan.core.util.Constants.mihonIntent
import ru.nyxsed.postscan.features.imagepager.presentation.ImagePagerArgs
import ru.nyxsed.postscan.features.imagepager.presentation.ImagePagerScreen

val CommentsScreen by navDestination<Post> {
    val args = navArgs()
    val commentsScreenViewModel = koinViewModel<CommentsScreenViewModel>(
        key = args.postId.toString(),
        parameters = { parametersOf(args) }
    )
    val navController = navController()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val comments by commentsScreenViewModel.comments.collectAsState()

    var settingUseMihon by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        settingUseMihon = commentsScreenViewModel.getSettingBoolean(SettingKey.USE_MIHON)
    }

    Scaffold { paddings ->
        if (comments.isEmpty()) {
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
                    items = comments
                        .filter { it.parentStack == null }
                        .filter { it.contentText.isNotEmpty() || it.content.isNotEmpty() },
                    key = { it.commentId }
                ) { originalComment ->
                    CommentCard(
                        comment = originalComment,
                        replays = comments
                            .filter { it.parentStack == originalComment.commentId }
                            .filter { it.contentText.isNotEmpty() || it.content.isNotEmpty() },
                        settingUseMihon = settingUseMihon,
                        onToMihonClicked = {
                            val intent = mihonIntent(
                                query = it.contentText
                            )
                            context.startActivity(intent)
                        },
                        onTextLongClick = {
                            clipboardManager.setText(
                                annotatedString = AnnotatedString(it)
                            )
                        },
                        onImageClicked = { content, index ->
                            val imagePagerArgs = ImagePagerArgs(content, index)
                            navController.navigate(ImagePagerScreen, imagePagerArgs)
                        }
                    )
                }
            }
        }
    }
}