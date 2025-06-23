package ru.nyxsed.postscan.features.posts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.ImagePagerArgs
import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.usecase.AddPostUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetAllGroupsUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetResourceUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetSettingBooleanFlowUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetSettingStringUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.core.domain.usecase.SetSettingBooleanUseCase
import ru.nyxsed.postscan.core.domain.usecase.SetSettingStringUseCase
import ru.nyxsed.postscan.core.domain.usecase.UpdateGroupUseCase
import ru.nyxsed.postscan.core.domain.util.NotificationHelper
import ru.nyxsed.postscan.core.event.UiEvent
import ru.nyxsed.postscan.core.event.UiEvent.*
import ru.nyxsed.postscan.features.comments.presentation.CommentsScreen
import ru.nyxsed.postscan.features.imagepager.presentation.ImagePagerScreen
import ru.nyxsed.postscan.features.login.presentation.LoginScreen
import ru.nyxsed.postscan.features.posts.domain.usecase.ChangePostLikeStatusUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.DeletePostUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.GetAllPostsUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.GetPostsForGroupUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.UpdatePostUseCase

class PostsViewModel(
    private val getResourceUseCase: GetResourceUseCase,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val isTokenValidUseCase: IsTokenValidUseCase,
    private val getSettingStringUseCase: GetSettingStringUseCase,
    private val setSettingStringUseCase: SetSettingStringUseCase,
    private val setSettingBooleanUseCase: SetSettingBooleanUseCase,
    private val getAllPostsUseCase: GetAllPostsUseCase,
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val addPostUseCase: AddPostUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val getPostsForGroupUseCase: GetPostsForGroupUseCase,
    private val changePostLikeStatusUseCase: ChangePostLikeStatusUseCase,
    private val notificationHelper: NotificationHelper,
    private val getSettingBooleanFlowUseCase: GetSettingBooleanFlowUseCase,
) : ViewModel() {

    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _state = MutableStateFlow(PostsState())
    val state = _state.asStateFlow()

    private val isLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            val settingSortOption =
                if (getSettingStringUseCase(SettingKey.SORT_OPTION) == "DESCENDING") SortOption.DESCENDING else SortOption.ASCENDING
            _state.update { it.copy(sortOption = settingSortOption) }
        }
        observeBooleanSettings()

        viewModelScope.launch {
            getAllPostsUseCase()
                .combine(isLoading) { posts, isLoading ->
                    if (isLoading) null else posts
                }
                .filterNotNull()
                .collect { posts ->
                    _state.update { it.copy(posts = posts) }
                }
        }
        viewModelScope.launch {
            getAllGroupsUseCase()
                .collect { groups ->
                    _state.update { it.copy(groups = groups) }
                }
        }
    }

    private fun observeBooleanSettings() {
        observeBooleanSetting(SettingKey.USE_MIHON) { setting ->
            _state.update { it.copy(settingUseMihon = setting) }
        }
        observeBooleanSetting(SettingKey.DELETE_AFTER_LIKE) { setting ->
            _state.update { it.copy(settingDeleteAfterLike = setting) }
        }
        observeBooleanSetting(SettingKey.SHOWED_TUTORIAL_POSTS) { setting ->
            _state.update { it.copy(showedTutorial = setting) }
        }
    }

    private fun observeBooleanSetting(key: SettingKey, apply: (Boolean) -> Unit) {
        viewModelScope.launch {
            getSettingBooleanFlowUseCase(key)
                .distinctUntilChanged()
                .collect { setting ->
                    apply(setting)
                }
        }
    }

    fun processIntent(intent: PostsIntent) {
        viewModelScope.launch {
            when (intent) {
                is PostsIntent.OpenUri -> _uiEventFlow.emit(OpenUrl(intent.query))
                is PostsIntent.OpenMihon -> _uiEventFlow.emit(OpenMihon(intent.query))
                is PostsIntent.CopyToClipboard -> _uiEventFlow.emit(CopyToClipboard(intent.text))
                is PostsIntent.SelectGroup -> _state.update { it.copy(selectedGroupId = intent.groupId) }
                is PostsIntent.NavigateToComments -> navigateToComments(intent.post)
                is PostsIntent.NavigateToImagePager -> {
                    val imagePagerArgs = ImagePagerArgs(intent.list, intent.index)
                    _uiEventFlow.emit(NavigateTo(ImagePagerScreen, imagePagerArgs))
                }

                is PostsIntent.ChangeSorting -> {
                    _state.update { it.copy(sortOption = intent.sortOption) }
                    setSettingStringUseCase(SettingKey.SORT_OPTION, intent.sortOption.toString())
                }

                is PostsIntent.Navigate -> _uiEventFlow.emit(NavigateTo(intent.destination))
                PostsIntent.RefreshPosts -> refreshPosts()
                PostsIntent.ShowedTutorial -> {
                    _state.update { it.copy(showedTutorial = true) }
                    setSettingBooleanUseCase(SettingKey.SHOWED_TUTORIAL_POSTS, true)
                }

                is PostsIntent.DeletePost -> deletePost(intent.post)
                is PostsIntent.ChangeLikeStatus -> changeLikeStatus(intent.post)
            }
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            notificationHelper.initNotification()
            _state.update { it.copy(showCircularIndicator = true) }
            isLoading.value = true
            try {
                _state.value.groups.forEachIndexed { index, group ->
                    val postEntities = getPostsForGroupUseCase(group)
                    postEntities.forEach { post ->
                        addPostUseCase(post)
                    }

                    val updatedGroup = group.copy(
                        lastFetchDate = (System.currentTimeMillis())
                    )
                    updateGroupUseCase(updatedGroup)

                    val percentage = (index + 1) * 100 / _state.value.groups.size
                    notificationHelper.updateProgressNotification(percentage)
                }
                notificationHelper.completeNotification()
                _state.update { it.copy(showCircularIndicator = false) }
                isLoading.value = false
            } catch (e: Exception) {
                _uiEventFlow.emit(ShowToast(e.message!!))
                notificationHelper.errorNotification(e.message!!)
                _state.update { it.copy(showCircularIndicator = false) }
                isLoading.value = false
            }
        }
    }

    private fun addPost(post: Post) {
        viewModelScope.launch {
            addPostUseCase(post)
        }
    }

    private fun deletePost(post: Post) {
        viewModelScope.launch {
            if (_state.value.posts.filter { it.ownerId == post.ownerId }.size == 1) {
                _state.update { it.copy(selectedGroupId = 0L) }
            }
            deletePostUseCase(post)
            _uiEventFlow.emit(
                ShowSnackbar(
                    messageID = R.string.post_deleted,
                    actionLabelID = R.string.undo,
                    onAction = {
                        addPost(post)
                    }
                )
            )
        }
    }

    private fun changeLikeStatus(
        post: Post,
    ) {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(ShowToast(getResourceUseCase(R.string.no_internet_connection)))
                return@launch
            }

            if (!isTokenValidUseCase()) {
                _uiEventFlow.emit(NavigateTo(LoginScreen))
                return@launch
            }
            try {
                changePostLikeStatusUseCase(post)
                if (_state.value.settingDeleteAfterLike) {
                    deletePost(post)
                } else {
                    updatePostUseCase(post.copy(isLiked = !post.isLiked))
                }
            } catch (e: Exception) {
                _uiEventFlow.emit(ShowToast(e.message!!))
            }
        }
    }

    private fun refreshPosts() {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(ShowToast(getResourceUseCase(R.string.no_internet_connection)))
                return@launch
            }

            if (!isTokenValidUseCase()) {
                _uiEventFlow.emit(NavigateTo(LoginScreen))
                return@launch
            }

            if (_state.value.groups.isEmpty()) {
                _uiEventFlow.emit(ShowToast(getResourceUseCase(R.string.groups_not_found)))
                return@launch
            }

            loadPosts()
            _uiEventFlow.emit(Scroll())
        }
    }

    private fun navigateToComments(post: Post) {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(ShowToast(getResourceUseCase(R.string.no_internet_connection)))
                return@launch
            }

            if (!isTokenValidUseCase()) {
                _uiEventFlow.emit(NavigateTo(LoginScreen))
                return@launch
            }

            _uiEventFlow.emit(NavigateTo(CommentsScreen, post))
        }
    }
}
