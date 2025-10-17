package ru.nyxsed.postscan.features.imagepager.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.composegears.tiamat.navArgs
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.ImagePagerArgs
import ru.nyxsed.postscan.core.event.CollectUiEvent
import ru.nyxsed.postscan.core.util.Constants.BING_SEARCH_URL
import ru.nyxsed.postscan.core.util.Constants.IQDB_SEARCH_URL
import ru.nyxsed.postscan.core.util.Constants.SAUCENAO_SEARCH_URL
import ru.nyxsed.postscan.core.util.Constants.TINEYE_SEARCH_URL
import ru.nyxsed.postscan.core.util.Constants.TRACE_SEARCH_URL
import ru.nyxsed.postscan.core.util.Constants.YANDEX_SEARCH_URL
import ru.nyxsed.postscan.uikit.ui.theme.LikedHeart

/**
 * Экран просмотра контента поста
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
val ImagePagerScreen by navDestination<ImagePagerArgs> {
    val imagePagerArgs = navArgs()
    val imagePagerViewModel: ImagePagerViewModel = koinViewModel(
        parameters = { parametersOf(imagePagerArgs.listContent, imagePagerArgs.index) },
        key = "${imagePagerArgs.hashCode()}"
    )
    val state by imagePagerViewModel.state.collectAsState()
    val navController = navController()

    val pagerState = rememberPagerState(
        initialPage = state.pageIndex,
        pageCount = { state.contentList.size }
    )

    LaunchedEffect(pagerState.currentPage) {
            imagePagerViewModel.processIntent(ImagePagerIntent.PageChanged(pagerState.currentPage))
    }

    CollectUiEvent(
        uiEventFlow = imagePagerViewModel.uiEventFlow,
        navController = navController,
    )

    ImagePagerContent(
        state = state,
        pagerState = pagerState,
        processIntent = {
            imagePagerViewModel.processIntent(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePagerContent(
    state: ImagePagerState,
    pagerState: PagerState,
    processIntent: (ImagePagerIntent) -> Unit,
) {
    IntroShowcase(
        showIntroShowCase = !state.showTutorial,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            processIntent(ImagePagerIntent.ImageTutorialCompleted)
        }
    ) {
        Scaffold(
            topBar = {
            }
        ) { paddings ->
            val scope = CoroutineScope(Dispatchers.Main)

            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .padding(paddings)
                    .fillMaxSize()
            ) {
                AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f),
                    visible = !state.fullScreen,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(),
                ) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Black.copy(alpha = 0.6f)
                        ),
                        title = {
                            Text(
                                text = "${pagerState.currentPage + 1} из ${state.contentList.size}",
                                color = Color.White
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    processIntent(ImagePagerIntent.NavigateBack)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                modifier = Modifier.introShowCaseTarget(
                                    index = 0,
                                    style = ShowcaseStyle.Default.copy(
                                        backgroundColor = Color(0xFF1C0A00),
                                        backgroundAlpha = 0.98f,
                                        targetCircleColor = Color.White
                                    ),
                                    content = {
                                        Column {
                                            Text(
                                                text = stringResource(R.string.tutorial_search_image),
                                                color = Color.White,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = stringResource(R.string.tutorial_search_image_desc),
                                                color = Color.White,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                ),
                                onClick = {
                                    processIntent(ImagePagerIntent.ToggleMenu)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = state.expendedMenu,
                                onDismissRequest = {
                                    processIntent(ImagePagerIntent.ToggleMenu)
                                }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(stringResource(R.string.image_search_source_saucenao))
                                    },
                                    onClick = {
                                        processIntent(
                                            ImagePagerIntent.FindImage(
                                                link = state.contentList[pagerState.currentPage].urlBig,
                                                source = SAUCENAO_SEARCH_URL
                                            )
                                        )
                                        processIntent(ImagePagerIntent.ToggleMenu)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(stringResource(R.string.image_search_source_yandex))
                                    },
                                    onClick = {
                                        processIntent(
                                            ImagePagerIntent.FindImage(
                                                link = state.contentList[pagerState.currentPage].urlBig,
                                                source = YANDEX_SEARCH_URL
                                            )
                                        )
                                        processIntent(ImagePagerIntent.ToggleMenu)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(stringResource(R.string.image_search_source_tracemoe))
                                    },
                                    onClick = {
                                        processIntent(
                                            ImagePagerIntent.FindImage(
                                                link = state.contentList[pagerState.currentPage].urlBig,
                                                source = TRACE_SEARCH_URL
                                            )
                                        )
                                        processIntent(ImagePagerIntent.ToggleMenu)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(stringResource(R.string.image_search_source_iqdb))
                                    },
                                    onClick = {
                                        processIntent(
                                            ImagePagerIntent.FindImage(
                                                link = state.contentList[pagerState.currentPage].urlBig,
                                                source = IQDB_SEARCH_URL
                                            )
                                        )
                                        processIntent(ImagePagerIntent.ToggleMenu)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(stringResource(R.string.image_search_source_tineye))
                                    },
                                    onClick = {
                                        processIntent(
                                            ImagePagerIntent.FindImage(
                                                link = state.contentList[pagerState.currentPage].urlBig,
                                                source = TINEYE_SEARCH_URL
                                            )
                                        )
                                        processIntent(ImagePagerIntent.ToggleMenu)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(stringResource(R.string.image_search_source_bing))
                                    },
                                    onClick = {
                                        processIntent(
                                            ImagePagerIntent.FindImage(
                                                link = state.contentList[pagerState.currentPage].urlBig,
                                                source = BING_SEARCH_URL
                                            )
                                        )
                                        processIntent(ImagePagerIntent.ToggleMenu)
                                    }
                                )
                            }
                        }
                    )
                }
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = pagerState,
                ) { index ->
                    ScalableCoilImage(
                        imageUrl = state.contentList[index].urlBig,
                        fullScreen = state.fullScreen,
                        onImageClicked = {
                            processIntent(ImagePagerIntent.ToggleFullScreen)
                        })
                }
                AnimatedVisibility(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    visible = !state.fullScreen,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                ) {
                    Column {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(
                                items = state.contentList,
                                key = { it.contentId }
                            ) { item ->
                                AsyncImage(
                                    modifier = Modifier
                                        .size(if (item.contentId == state.contentList[pagerState.currentPage].contentId) 60.dp else 30.dp)
                                        .clickable(onClick = {
                                            scope.launch {
                                                val currentIndex = state.contentList.indexOf(item)
                                                pagerState.scrollToPage(page = currentIndex)
                                            }
                                        }),
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(item.urlSmall)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(8.dp),
                        ) {
                            IconButton(
                                onClick = {
                                    processIntent(ImagePagerIntent.OpenPostUri(state.contentList[state.pageIndex]))
                                }
                            ) {
                                Image(
                                    modifier = Modifier
                                        .size(24.dp),
                                    painter = painterResource(R.drawable.vk_logo),
                                    contentDescription = null
                                )
                            }
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    processIntent(ImagePagerIntent.LikeClicked(pagerState.currentPage))
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_like),
                                    tint = when (state.contentList.getOrNull(pagerState.currentPage)?.isLiked) {
                                        true -> LikedHeart
                                        false, null -> Color.White
                                    },
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ScalableCoilImage(
    imageUrl: String,
    fullScreen: Boolean,
    onImageClicked: () -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (fullScreen) {
                    Modifier.pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            offset += pan
                        }
                    }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                } else {
                    Modifier
                }
            )

    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = {
                    onImageClicked()
                }),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(70.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            contentScale = ContentScale.Fit
        )
    }
}
