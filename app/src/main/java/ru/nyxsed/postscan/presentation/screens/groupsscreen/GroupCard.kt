package ru.nyxsed.postscan.presentation.screens.groupsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import ru.nyxsed.postscan.data.models.entity.GroupEntity
import ru.nyxsed.postscan.presentation.ui.theme.Green80
import ru.nyxsed.postscan.presentation.ui.theme.SoftGreen80
import ru.nyxsed.postscan.util.Constants.toStringDate

@Composable
fun GroupCard(
    group: GroupEntity,
    deleteEnabled: Boolean,
    existingGroup: Boolean = false,
    onGroupDeleteClicked: (GroupEntity) -> Unit,
    onGroupClicked: (GroupEntity) -> Unit,
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = if (existingGroup) SoftGreen80 else MaterialTheme.colorScheme.secondary)
                .padding(8.dp)
                .clickable {
                    onGroupClicked(group)
                }
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
                        .size(50.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(group.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                )
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = group.name,
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (deleteEnabled) {
                        Text(
                            text = group.lastFetchDate.toStringDate(),
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
                if (deleteEnabled) {
                    IconButton(
                        onClick = {
                            onGroupDeleteClicked(group)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                } else if (existingGroup) {
                    Icon(
                        painter = painterResource(R.drawable.ic_checkmark),
                        contentDescription = null,
                        tint = Green80
                    )
                }
            }
        }
    }
}