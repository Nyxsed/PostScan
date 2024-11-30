package ru.nyxsed.postscan.presentation.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.data.models.entity.ContentEntity

@Composable
fun MultipleImages(
    listContent: List<ContentEntity>,
    onImageClicked: (Int) -> Unit,
) {

    when (listContent.size) {
        0 -> {
            return
        }

        2 -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ImageItem(modifier = Modifier.weight(1f), content = listContent[0], onImageClicked = { onImageClicked(0) })
                ImageItem(modifier = Modifier.weight(1f), content = listContent[1], onImageClicked = { onImageClicked(1) })
            }
        }

        3 -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ImageItem(modifier = Modifier.weight(1f), content = listContent[0], onImageClicked = { onImageClicked(0) })
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[1], onImageClicked = { onImageClicked(1) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[2], onImageClicked = { onImageClicked(2) })
                }
            }
        }

        4 -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[0], onImageClicked = { onImageClicked(0) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[1], onImageClicked = { onImageClicked(1) })
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[2], onImageClicked = { onImageClicked(2) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[3], onImageClicked = { onImageClicked(3) })
                }
            }
        }

        5 -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[0], onImageClicked = { onImageClicked(0) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[1], onImageClicked = { onImageClicked(1) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[2], onImageClicked = { onImageClicked(2) })
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[3], onImageClicked = { onImageClicked(3) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[4], onImageClicked = { onImageClicked(4) })
                }
            }
        }

        6 -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[0], onImageClicked = { onImageClicked(0) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[1], onImageClicked = { onImageClicked(1) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[2], onImageClicked = { onImageClicked(2) })
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[3], onImageClicked = { onImageClicked(3) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[4], onImageClicked = { onImageClicked(4) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[5], onImageClicked = { onImageClicked(5) })
                }
            }
        }

        7 -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[0], onImageClicked = { onImageClicked(0) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[1], onImageClicked = { onImageClicked(1) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[2], onImageClicked = { onImageClicked(2) })
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[3], onImageClicked = { onImageClicked(3) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[4], onImageClicked = { onImageClicked(4) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[5], onImageClicked = { onImageClicked(5) })
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[6], onImageClicked = { onImageClicked(6) })
                }
            }
        }

        8 -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[0], onImageClicked = { onImageClicked(0) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[1], onImageClicked = { onImageClicked(1) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[2], onImageClicked = { onImageClicked(2) })
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[3], onImageClicked = { onImageClicked(3) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[4], onImageClicked = { onImageClicked(4) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[5], onImageClicked = { onImageClicked(5) })
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[6], onImageClicked = { onImageClicked(6) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[7], onImageClicked = { onImageClicked(7) })
                }
            }
        }

        9 -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[0], onImageClicked = { onImageClicked(0) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[1], onImageClicked = { onImageClicked(1) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[2], onImageClicked = { onImageClicked(2) })
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[3], onImageClicked = { onImageClicked(3) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[4], onImageClicked = { onImageClicked(4) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[5], onImageClicked = { onImageClicked(5) })
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[6], onImageClicked = { onImageClicked(6) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[7], onImageClicked = { onImageClicked(7) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[8], onImageClicked = { onImageClicked(8) })
                }
            }
        }

        10 -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[0], onImageClicked = { onImageClicked(0) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[1], onImageClicked = { onImageClicked(1) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[2], onImageClicked = { onImageClicked(2) })
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[3], onImageClicked = { onImageClicked(3) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[4], onImageClicked = { onImageClicked(4) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[5], onImageClicked = { onImageClicked(5) })
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[6], onImageClicked = { onImageClicked(6) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[7], onImageClicked = { onImageClicked(7) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[8], onImageClicked = { onImageClicked(8) })
                    ImageItem(modifier = Modifier.weight(1f), content = listContent[9], onImageClicked = { onImageClicked(9) })
                }
            }
        }

        else -> {
            Row {
                ImageItem(modifier = Modifier.weight(1f), content = listContent[0], onImageClicked = { onImageClicked(0) })
            }

        }
    }
}

@Composable
fun ImageItem(
    modifier: Modifier,
    content: ContentEntity,
    onImageClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(1.dp)
            .clickable(onClick = {
                onImageClicked()
            }),
        contentAlignment = Alignment.TopEnd
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = content.urlMedium,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_placeholder),
            contentScale = ContentScale.Crop
        )
        if (content.type in listOf("video", "album")) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .size(30.dp)
                    .background(MaterialTheme.colorScheme.secondary)
                    .alpha(0.7f)
            ) {
                Icon(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize(),
                    imageVector = if (content.type == "video") Icons.Filled.PlayArrow else Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}