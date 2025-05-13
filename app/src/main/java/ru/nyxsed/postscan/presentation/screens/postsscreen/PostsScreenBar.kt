package ru.nyxsed.postscan.presentation.screens.postsscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import ru.nyxsed.postscan.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreenBar(
    onRefreshClicked: () -> Unit,
    onNavToGroupsClicked: () -> Unit,
    onNavToSettingsClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    showShowcase: Boolean,
    onShowcaseShowed: () -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = "Post Scan",
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        scrollBehavior = scrollBehavior,
        actions = {
            IntroShowcase(
                showIntroShowCase = showShowcase,
                dismissOnClickOutside = true,
                onShowCaseCompleted = onShowcaseShowed
            ) {
                IconButton(
                    onClick = onRefreshClicked,
                    modifier = Modifier.introShowCaseTarget(
                        index = 1,
                        style = ShowcaseStyle.Default.copy(
                            backgroundColor = Color(0xFF1C0A00), // specify color of background
                            backgroundAlpha = 0.98f, // specify transparency of background
                            targetCircleColor = Color.White
                        ),
                        content = {
                            Column {
                                Text(
                                    text = stringResource(R.string.tutorial_download),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.tutorial_download_desc),
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    ),
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_download),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null,
                    )
                }
                IconButton(
                    onClick = onNavToGroupsClicked,
                    modifier = Modifier.introShowCaseTarget(
                        index = 0,
                        style = ShowcaseStyle.Default.copy(
                            backgroundColor = Color(0xFF1C0A00), // specify color of background
                            backgroundAlpha = 0.98f, // specify transparency of background
                            targetCircleColor = Color.White
                        ),
                        content = {
                            Column {
                                Text(
                                    text = stringResource(R.string.tutorial_groups),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.tutorial_groups_desc),
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    ),
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_groups),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null,
                    )
                }
                IconButton(
                    onClick = onNavToSettingsClicked,
                    modifier = Modifier.introShowCaseTarget(
                        index = 2,
                        style = ShowcaseStyle.Default.copy(
                            backgroundColor = Color(0xFF1C0A00), // specify color of background
                            backgroundAlpha = 0.98f, // specify transparency of background
                            targetCircleColor = Color.White
                        ),
                        content = {
                            Column {
                                Text(
                                    text = stringResource(R.string.tutorial_settings),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.tutorial_settings_desc),
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    ),
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_settings),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null,
                    )
                }
            }
        }
    )
}