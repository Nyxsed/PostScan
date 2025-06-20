package ru.nyxsed.postscan.features.imagepager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.Content
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.usecase.GetResourceUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetSettingBooleanUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.core.domain.usecase.SetSettingBooleanUseCase
import ru.nyxsed.postscan.core.event.UiEvent
import ru.nyxsed.postscan.core.event.UiEvent.*
import ru.nyxsed.postscan.core.util.Constants.VK_PHOTO_URL
import ru.nyxsed.postscan.features.imagepager.domain.usecase.ChangeContentLikeStatusUseCase
import ru.nyxsed.postscan.features.imagepager.domain.usecase.CheckContentLikeStatusUseCase
import ru.nyxsed.postscan.features.login.presentation.LoginScreen
import java.net.URLEncoder

class ImagePagerViewModel(
    private val getResourceUseCase: GetResourceUseCase,
    private val getSettingBooleanUseCase: GetSettingBooleanUseCase,
    private val setSettingBooleanUseCase: SetSettingBooleanUseCase,
    private val checkContentLikeStatusUseCase: CheckContentLikeStatusUseCase,
    private val changeContentLikeStatusUseCase: ChangeContentLikeStatusUseCase,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val isTokenValidUseCase: IsTokenValidUseCase,
    private val contentList: List<Content>,
    private val pageIndex: Int,
) : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _state = MutableStateFlow(ImagePagerState(
        contentList = contentList,
        pageIndex = pageIndex
    ))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val setting = getSettingBooleanUseCase(SettingKey.SHOWED_TUTORIAL_IMAGE)
            _state.update { it.copy(showTutorial = setting) }
        }
    }

    fun processIntent(intent: ImagePagerIntent) {
        viewModelScope.launch {
            when (intent) {
                ImagePagerIntent.ToggleFullScreen -> _state.update { it.copy(fullScreen = !it.fullScreen) }
                ImagePagerIntent.ToggleMenu -> _state.update { it.copy(expendedMenu = !it.expendedMenu) }
                ImagePagerIntent.ImageTutorialCompleted -> setSettingBooleanUseCase(SettingKey.SHOWED_TUTORIAL_IMAGE, true)
                ImagePagerIntent.NavigateBack -> _uiEventFlow.emit(NavigateBack())
                is ImagePagerIntent.FindImage -> findImage(intent.link, intent.source)
                is ImagePagerIntent.OpenPostUri -> openPostUri(intent.content)
                is ImagePagerIntent.PageChanged -> changePage(intent.pageIndex)
                is ImagePagerIntent.LikeClicked -> likeClicked(intent.pageIndex)
            }
        }
    }

    private suspend fun likeClicked(index: Int) {
        val connected = checkConnect()
        if (!connected) {
            _uiEventFlow.emit(UiEvent.NavigateTo(LoginScreen))
            return
        }

        val entity = _state.value.contentList.getOrNull(index) ?: return

        try {
            changeContentLikeStatusUseCase(entity)
        } catch (e: Exception) {
            _uiEventFlow.emit(UiEvent.ShowToast(e.message!!))
            return
        }

        val updatedList = _state.value.contentList.mapIndexed { i, item ->
            if (i == index) item.copy(isLiked = !item.isLiked) else item
        }

        _state.value = _state.value.copy(contentList = updatedList)
    }

    private suspend fun changePage(index: Int) {
        val connected = checkConnect()
        if (!connected) return

        if (_state.value.pageData.containsKey(index)) return

        val entity = _state.value.contentList.getOrNull(index) ?: return
        val isLiked = checkContentLikeStatusUseCase(entity)

        val updatedList = _state.value.contentList.mapIndexed { i, item ->
            if (i == index) item.copy(isLiked = isLiked) else item
        }

        _state.update { it.copy(contentList = updatedList, pageData = _state.value.pageData + (index to isLiked)) }
    }

    private suspend fun openPostUri(content: Content) {
        _uiEventFlow.emit(UiEvent.OpenUrl("${VK_PHOTO_URL}${content.ownerId}_${content.contentId}"))
    }

    private suspend fun findImage(link: String, source: String) {
        val endLink = URLEncoder.encode(link, "utf-8")
        _uiEventFlow.emit(UiEvent.OpenUrl("$source$endLink"))
    }

    private suspend fun checkConnect(): Boolean {
        if (!isInternetAvailableUseCase()) {
            _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.no_internet_connection)))
            return false
        }

        if (!isTokenValidUseCase()) {
            _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.token_is_invalid)))
            return false
        }
        return true
    }
}