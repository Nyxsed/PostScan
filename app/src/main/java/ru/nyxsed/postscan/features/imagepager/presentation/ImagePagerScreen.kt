package ru.nyxsed.postscan.features.imagepager.presentation

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalUriHandler
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
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.models.entity.Content
import ru.nyxsed.postscan.core.util.Constants.BING_SEARCH_URL
import ru.nyxsed.postscan.core.util.Constants.IQDB_SEARCH_URL
import ru.nyxsed.postscan.core.util.Constants.SAUCENAO_SEARCH_URL
import ru.nyxsed.postscan.core.util.Constants.TINEYE_SEARCH_URL
import ru.nyxsed.postscan.core.util.Constants.TRACE_SEARCH_URL
import ru.nyxsed.postscan.core.util.Constants.YANDEX_SEARCH_URL
import ru.nyxsed.postscan.core.util.UiEvent
import ru.nyxsed.postscan.uikit.ui.theme.LikedHeart

@OptIn(ExperimentalMaterial3Api::class)
val ImagePagerScreen by navDestination<ImagePagerArgs> {
    val imagePagerArgs = navArgs()
    val index = imagePagerArgs.index
    val imagePagerViewModel = koinViewModel<ImagePagerViewModel>()
    val context = LocalContext.current
    val navController = navController()
    val scope = CoroutineScope(Dispatchers.Main)

    var content by remember { mutableStateOf<List<Content>>(imagePagerArgs.listContent) }

    val uriHandler = LocalUriHandler.current
    val pagerState = rememberPagerState(
        initialPage = index,
        pageCount = { content.size }
    )

    var pageData by remember { mutableStateOf<Map<Int, Boolean>>(emptyMap()) }

    var showedTutorial by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        showedTutorial = imagePagerViewModel.getSettingBoolean(SettingKey.SHOWED_TUTORIAL_IMAGE)
        imagePagerViewModel.uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                is UiEvent.Navigate ->
                    navController.navigate(event.destination)

                else -> {}
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val connect = imagePagerViewModel.checkConnect()
        if (!connect) return@LaunchedEffect

        val currentPage = pagerState.currentPage
        if (!pageData.containsKey(currentPage)) {

            val data = imagePagerViewModel.checkLikeStatus(content[currentPage])

            val updatedContent = content.mapIndexed { index, entity ->
                if (index == currentPage) {
                    entity.copy(isLiked = data)
                } else {
                    entity
                }
            }
            content = updatedContent

            pageData = pageData + (currentPage to data)
        }
    }

    var notFullScreen by remember { mutableStateOf(true) }

    Log.d("showedTutorial", "$showedTutorial")

    IntroShowcase(
        showIntroShowCase = !showedTutorial,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            imagePagerViewModel.setSettingBoolean(SettingKey.SHOWED_TUTORIAL_IMAGE, true)
        }
    ) {
        Scaffold(
            topBar = {
            }
        ) { paddings ->

            var menuExpanded by remember { mutableStateOf(false) }

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
                    visible = notFullScreen,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(),
                ) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Black.copy(alpha = 0.6f)
                        ),
                        title = {
                            Text(
                                text = "${pagerState.currentPage + 1} из ${content.size}",
                                color = Color.White
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    navController.back()
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
                                        backgroundColor = Color(0xFF1C0A00), // specify color of background
                                        backgroundAlpha = 0.98f, // specify transparency of background
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
                                    menuExpanded = true
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = {
                                    menuExpanded = false
                                }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text("SauceNAO")
                                    },
                                    onClick = {
                                        imagePagerViewModel.findImage(
                                            uriHandler,
                                            content[pagerState.currentPage].urlBig,
                                            SAUCENAO_SEARCH_URL
                                        )
                                        menuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text("Yandex")
                                    },
                                    onClick = {
                                        imagePagerViewModel.findImage(
                                            uriHandler,
                                            content[pagerState.currentPage].urlBig,
                                            YANDEX_SEARCH_URL
                                        )
                                        menuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text("TraceMoe")
                                    },
                                    onClick = {
                                        imagePagerViewModel.findImage(
                                            uriHandler,
                                            content[pagerState.currentPage].urlBig,
                                            TRACE_SEARCH_URL
                                        )
                                        menuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text("IQDB")
                                    },
                                    onClick = {
                                        imagePagerViewModel.findImage(
                                            uriHandler,
                                            content[pagerState.currentPage].urlBig,
                                            IQDB_SEARCH_URL
                                        )
                                        menuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text("Tineye")
                                    },
                                    onClick = {
                                        imagePagerViewModel.findImage(
                                            uriHandler,
                                            content[pagerState.currentPage].urlBig,
                                            TINEYE_SEARCH_URL
                                        )
                                        menuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text("Bing")
                                    },
                                    onClick = {
                                        imagePagerViewModel.findImage(
                                            uriHandler,
                                            content[pagerState.currentPage].urlBig,
                                            BING_SEARCH_URL
                                        )
                                        menuExpanded = false
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
                        imageUrl = content[index].urlBig,
                        fullScreen = !notFullScreen,
                        onImageClicked = {
                            notFullScreen = !notFullScreen
                        })
                }
                AnimatedVisibility(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    visible = notFullScreen,
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
                                items = content,
                                key = { it.contentId }
                            ) { item ->
                                AsyncImage(
                                    modifier = Modifier
                                        .size(if (item.contentId == content[pagerState.currentPage].contentId) 60.dp else 30.dp)
                                        .clickable(onClick = {
                                            scope.launch {
                                                val currentIndex = content.indexOf(item)
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
                                    imagePagerViewModel.openPostUri(
                                        uriHandler = uriHandler,
                                        content = content[index]
                                    )
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
                                    scope.launch {
                                        val connect = imagePagerViewModel.checkConnect()
                                        if (!connect) {
                                            imagePagerViewModel.navigateToLogin()
                                            return@launch
                                        }

                                        imagePagerViewModel.changeLikeStatus(content[pagerState.currentPage])

                                        val updatedContent = content.mapIndexed { index, entity ->
                                            if (index == pagerState.currentPage) {
                                                entity.copy(isLiked = !entity.isLiked)
                                            } else {
                                                entity
                                            }
                                        }
                                        content = updatedContent
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_like),
                                    tint = if (content[pagerState.currentPage].isLiked) LikedHeart else Color.White,
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


data class ImagePagerArgs(
    val listContent: List<Content>,
    val index: Int,
)

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
                            scale = (scale * zoom).coerceIn(1f, 5f) // Ограничение масштаба
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
            contentScale = ContentScale.Fit // Масштабируем содержимое по Crop
        )
    }
}
