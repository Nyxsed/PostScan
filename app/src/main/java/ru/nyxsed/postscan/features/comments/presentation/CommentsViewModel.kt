package ru.nyxsed.postscan.features.comments.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.core.domain.models.ImagePagerArgs
import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.usecase.GetSettingBooleanUseCase
import ru.nyxsed.postscan.core.event.UiEvent
import ru.nyxsed.postscan.features.comments.domain.usecase.GetCommentsUseCase
import ru.nyxsed.postscan.features.imagepager.presentation.ImagePagerScreen

class CommentsViewModel(
    private val post: Post,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getSettingBooleanUseCase: GetSettingBooleanUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CommentsState())
    val state = _state.asStateFlow()

    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            val useMihon = getSettingBooleanUseCase(SettingKey.USE_MIHON)
            getCommentsUseCase(post).collect { comments ->
                _state.update {
                    it.copy(
                        comments = comments,
                        settingMihon = useMihon,
                    )
                }
            }
        }
    }

    fun processIntent(commentsIntent: CommentsIntent) {
        viewModelScope.launch {
            when (commentsIntent) {
                is CommentsIntent.OnImageClicked -> {
                    val imagePagerArgs = ImagePagerArgs(commentsIntent.list, commentsIntent.index)
                    _uiEventFlow.emit(UiEvent.NavigateTo(ImagePagerScreen, imagePagerArgs))
                }

                is CommentsIntent.OnMihonClicked -> _uiEventFlow.emit(UiEvent.OpenMihon(commentsIntent.text))

                is CommentsIntent.OnTextLongClick -> _uiEventFlow.emit(UiEvent.CopyToClipboard(commentsIntent.text))
            }
        }
    }
}