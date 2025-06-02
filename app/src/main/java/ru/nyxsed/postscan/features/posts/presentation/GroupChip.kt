package ru.nyxsed.postscan.features.posts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import ru.nyxsed.postscan.core.domain.models.entity.Group
import ru.nyxsed.postscan.uikit.ui.theme.VkBlue

@Composable
fun GroupChip(
    group: Group,
    postCount: Int,
    isSelected: Boolean,
    onChipClicked: () -> Unit,
) {
    val backgroundColor = if (isSelected) VkBlue else MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onPrimary

    val displayedText = if (group.name.length > 10) {
        group.name.substring(0, 10) + "..."
    } else {
        group.name
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
            .border(1.dp, color = textColor, shape = RoundedCornerShape(16.dp))
            .clickable { onChipClicked() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp),
                contentAlignment = Alignment.Center,
            )
            {
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(25.dp)
                        .alpha(0.4f),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(group.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                )
                Text(
                    text = postCount.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = textColor,
                    style = TextStyle.Default.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f,2f),
                            blurRadius = 3f
                        )
                    )
                )
            }
            Text(
                modifier = Modifier
                    .padding(4.dp),
                text = displayedText.uppercase(),
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}