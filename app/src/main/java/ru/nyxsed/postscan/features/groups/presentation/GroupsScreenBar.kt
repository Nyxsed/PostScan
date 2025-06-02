package ru.nyxsed.postscan.features.groups.presentation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.nyxsed.postscan.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreenBar(
    onDownloadClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = "Groups",
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(
                onClick = onDownloadClicked
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_download),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null,
                )
            }
            IconButton(
                onClick = onDeleteClicked
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Filled.Delete,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null,
                )
            }
        }
    )
}