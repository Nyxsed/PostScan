package ru.nyxsed.postscan.presentation.screens.postsscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.common.domain.models.entity.ContentEntity
import ru.nyxsed.postscan.common.domain.models.entity.PostEntity
import ru.nyxsed.postscan.presentation.elements.MultipleImages
import ru.nyxsed.postscan.presentation.ui.theme.LikedHeart
import ru.nyxsed.postscan.presentation.ui.theme.VkBlue
import ru.nyxsed.postscan.util.Constants.toStringDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostCard(
    post: PostEntity,
    settingUseMihon: Boolean,
    onPostDeleteClicked: (PostEntity) -> Unit,
    onLikeClicked: (PostEntity) -> Unit,
    onToVkClicked: (PostEntity) -> Unit,
    onToMihonClicked: (String) -> Unit,
    onTextLongClick: (PostEntity) -> Unit,
    onImageClicked: (List<ContentEntity>, Int) -> Unit,
    onCommentsClicked: (PostEntity) -> Unit,
    onGroupClicked: (PostEntity) -> Unit,
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondary)
                .padding(8.dp)
        ) {
            PostHeader(
                post = post,
                onGroupClicked = onGroupClicked
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            ) {
                MultipleImages(
                    listContent = post.content,
                    onImageClicked = {
                        onImageClicked(post.content, it)
                    }
                )
                if (post.content.size > 10) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp, end = 8.dp)
                                .clip(CircleShape)
                                .size(25.dp)
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier,
                                text = post.content.size.toString(),
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                }
            }
            if (post.contentText.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { },
                            onLongClick = {
                                onTextLongClick(post)
                            }
                        ),
                    text = post.contentText,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
            if (post.content.filter { it.type == "album" }.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    text = stringResource(R.string.albums),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
                post.content
                    .filter { it.type == "album" }
                    .forEach {
                        Row(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(lerp(MaterialTheme.colorScheme.secondary, Color.Black, 0.1f)),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .combinedClickable(
                                        onClick = { },
                                        onLongClick = {
                                            onTextLongClick(post)
                                        }
                                    ),
                                text = it.title,
                                color = MaterialTheme.colorScheme.onSecondary,
                            )
                            if (settingUseMihon) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp),
                                    onClick = {
                                        onToMihonClicked(it.title)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_mihon),
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        onToVkClicked(post)
                    }
                ) {
                    Image(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(R.drawable.vk_logo),
                        contentDescription = null
                    )
                }
                if (settingUseMihon && post.contentText.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onToMihonClicked(post.contentText)
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(R.drawable.ic_mihon),
                            tint = MaterialTheme.colorScheme.onSecondary,
                            contentDescription = null
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
                IconButton(
                    onClick = {
                        onPostDeleteClicked(post)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        tint = MaterialTheme.colorScheme.onSecondary,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {
                        onCommentsClicked(post)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_comment),
                        tint = MaterialTheme.colorScheme.onSecondary,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {
                        onLikeClicked(post)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_like),
                        tint = if (post.isLiked) LikedHeart else MaterialTheme.colorScheme.onSecondary,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun PostHeader(
    post: PostEntity,
    onGroupClicked: (PostEntity) -> Unit,
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
                .size(50.dp)
                .clickable(
                    onClick = {
                        onGroupClicked(post)
                    }
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(post.ownerImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
        )
        if (post.haveReposts) {
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(25.dp),
                painter = painterResource(R.drawable.ic_arrow_repost),
                contentDescription = null,
                tint = VkBlue
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = post.ownerName,
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = post.publicationDate.toStringDate(),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}