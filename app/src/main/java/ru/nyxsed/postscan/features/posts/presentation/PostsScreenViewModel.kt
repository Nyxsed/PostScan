package ru.nyxsed.postscan.features.posts.presentation

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.core.domain.models.entity.PostEntity
import ru.nyxsed.postscan.core.domain.usecase.AddPostUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetAllGroupsUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetResourceUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetSettingBooleanUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetSettingStringUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.core.domain.usecase.SetSettingBooleanUseCase
import ru.nyxsed.postscan.core.domain.usecase.SetSettingStringUseCase
import ru.nyxsed.postscan.core.domain.usecase.UpdateGroupUseCase
import ru.nyxsed.postscan.core.domain.util.NotificationHelper
import ru.nyxsed.postscan.core.util.Constants.VK_URL
import ru.nyxsed.postscan.core.util.Constants.VK_WALL_URL
import ru.nyxsed.postscan.core.util.UiEvent
import ru.nyxsed.postscan.features.comments.presentation.CommentsScreen
import ru.nyxsed.postscan.features.login.presentation.LoginScreen
import ru.nyxsed.postscan.features.posts.domain.usecase.ChangePostLikeStatusUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.DeletePostUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.GetAllPostsUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.GetPostsForGroupUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.UpdatePostUseCase

class PostsScreenViewModel(
    private val getResourceUseCase: GetResourceUseCase,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val isTokenValidUseCase: IsTokenValidUseCase,
    private val getSettingStringUseCase: GetSettingStringUseCase,
    private val setSettingStringUseCase: SetSettingStringUseCase,
    private val getSettingBooleanUseCase: GetSettingBooleanUseCase,
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
) : ViewModel() {
    val posts = getAllPostsUseCase()
    val groups = getAllGroupsUseCase()

    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _groupSelected = MutableStateFlow<Long>(0L)
    val groupSelected: StateFlow<Long> = _groupSelected.asStateFlow()

    private val _sortOption = MutableStateFlow<SortOption?>(null)
    val sortOption: StateFlow<SortOption?> = _sortOption.asStateFlow()

    init {
        viewModelScope.launch {
            val setting = getSetting(SettingKey.SORT_OPTION)
            _sortOption.value = if (setting == "DESCENDING") SortOption.DESCENDING else SortOption.ASCENDING
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            notificationHelper.initNotification()
            _uiEventFlow.emit(UiEvent.UpdateStatus(true))
            try {
                groups.value.forEachIndexed { index, group ->
                    val postEntities = getPostsForGroupUseCase(group)
                    postEntities.forEach { post ->
                        addPostUseCase(post)
                    }

                    val updatedGroup = group.copy(
                        lastFetchDate = (System.currentTimeMillis())
                    )
                    updateGroupUseCase(updatedGroup)

                    val percentage = (index + 1) * 100 / groups.value.size
                    notificationHelper.updateProgressNotification(percentage)
                }
                notificationHelper.completeNotification()
                _uiEventFlow.emit(UiEvent.UpdateStatus(false))
            } catch (e: Exception) {
                _uiEventFlow.emit(UiEvent.ShowToast(e.message!!))
                notificationHelper.errorNotification(e.message!!)
                _uiEventFlow.emit(UiEvent.UpdateStatus(false))
            }
        }
    }

    fun addPost(post: PostEntity) {
        viewModelScope.launch {
            addPostUseCase(post)
        }
    }

    fun deletePost(post: PostEntity, context: Context, snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            if (posts.value.filter { it.ownerId == post.ownerId }.size == 1) {
                selectGroup(0L)
            }
            deletePostUseCase(post)

            snackbarHostState.currentSnackbarData?.dismiss()
            val snackbarResult = snackbarHostState.showSnackbar(
                message = context.getString(R.string.post_deleted),
                actionLabel = context.getString(R.string.undo),
                duration = SnackbarDuration.Short
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                addPost(post)
            }
        }
    }

    fun changeLikeStatus(
        post: PostEntity,
        settingDeleteAfterLike: Boolean,
        context: Context,
        snackbarHostState: SnackbarHostState,
    ) {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.no_internet_connection)))
                return@launch
            }

            if (!isTokenValidUseCase()) {
                _uiEventFlow.emit(UiEvent.Navigate(LoginScreen))
                return@launch
            }
            try {
                changeLikeStatusVK(post)
                if (settingDeleteAfterLike) {
                    deletePost(post, context, snackbarHostState)
                } else {
                    changeLikeStatusDb(post)
                }
            } catch (e: Exception) {
                _uiEventFlow.emit(UiEvent.ShowToast(e.message!!))
            }
        }
    }

    suspend fun changeLikeStatusVK(post: PostEntity) {
        changePostLikeStatusUseCase(post)
    }

    suspend fun changeLikeStatusDb(post: PostEntity) {
        updatePostUseCase(post.copy(isLiked = !post.isLiked))
    }

    fun openPostUri(uriHandler: UriHandler, post: PostEntity) {
        uriHandler.openUri("${VK_WALL_URL}${post.ownerId}_${post.postId}")
    }

    fun openGroupUri(uriHandler: UriHandler, group: GroupEntity) {
        uriHandler.openUri("${VK_URL}${group.screenName}")
    }

    suspend fun getSettingBoolean(key: SettingKey): Boolean {
        return getSettingBooleanUseCase(key)
    }

    fun setSettingBoolean(key: SettingKey, value: Boolean) {
        viewModelScope.launch {
            setSettingBooleanUseCase(key, value)
        }
    }

    suspend fun getSetting(key: SettingKey): String {
        return getSettingStringUseCase(key)
    }

    fun setSetting(key: SettingKey, value: String) {
        viewModelScope.launch {
            setSettingStringUseCase(key, value)
        }
    }

    fun refreshPosts(context: Context) {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.no_internet_connection)))
                return@launch
            }

            if (!isTokenValidUseCase()) {
                _uiEventFlow.emit(UiEvent.Navigate(LoginScreen))
                return@launch
            }

            if (groups.value.isEmpty()) {
                _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.groups_not_found)))
                return@launch
            }

            loadPosts()
            _uiEventFlow.emit(UiEvent.Scroll())
        }
    }

    fun toComments(post: PostEntity) {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.no_internet_connection)))
                return@launch
            }

            if (!isTokenValidUseCase()) {
                _uiEventFlow.emit(UiEvent.Navigate(LoginScreen))
                return@launch
            }

            _uiEventFlow.emit(UiEvent.NavigateToPost(CommentsScreen, post))
        }
    }

    fun selectGroup(groupId: Long) {
        _groupSelected.value = groupId
    }

    fun changeSorting(sortOption: SortOption) {
        _sortOption.value = sortOption
        setSetting(SettingKey.SORT_OPTION, sortOption.toString())
    }
}
