package ru.nyxsed.postscan.common.presentation.screens.commentsscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.common.domain.models.entity.CommentEntity
import ru.nyxsed.postscan.common.domain.models.entity.ContentEntity
import ru.nyxsed.postscan.common.presentation.elements.MultipleImages
import ru.nyxsed.postscan.common.presentation.ui.theme.VkBlue
import ru.nyxsed.postscan.common.util.Constants.toStringDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentCard(
    comment: CommentEntity,
    replays: List<CommentEntity>,
    settingUseMihon: Boolean,
    onToMihonClicked: (CommentEntity) -> Unit,
    onTextLongClick: (String) -> Unit,
    onImageClicked: (List<ContentEntity>, Int) -> Unit,
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondary)
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(30.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(comment.ownerImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                )
                Column(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = comment.ownerName,
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = comment.publicationDate.toStringDate(),
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                if (settingUseMihon) {
                    IconButton(
                        onClick = {
                            onToMihonClicked(comment)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_mihon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
            MultipleImages(
                listContent = comment.content,
                onImageClicked = {
                    onImageClicked(comment.content, it)
                }
            )
            Text(
                modifier = Modifier
                    .combinedClickable(
                        onClick = { },
                        onLongClick = {
                            onTextLongClick(comment.contentText)
                        }
                    ),
                text = comment.contentText,
                color = MaterialTheme.colorScheme.onSecondary
            )
            replays.forEach { reply ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(25.dp),
                        painter = painterResource(R.drawable.ic_arrow_repost),
                        contentDescription = null,
                        tint = VkBlue
                    )
                    AsyncImage(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .size(25.dp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(reply.ownerImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                    )
                    MultipleImages(
                        listContent = reply.content,
                        onImageClicked = {
                            onImageClicked(reply.content, it)
                        }
                    )
                    Text(
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f)
                            .combinedClickable(
                                onClick = { },
                                onLongClick = {
                                    onTextLongClick(reply.contentText)
                                }
                            ),
                        text = reply.contentText,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}