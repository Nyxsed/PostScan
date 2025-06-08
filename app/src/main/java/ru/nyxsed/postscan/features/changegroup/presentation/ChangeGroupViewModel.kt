package ru.nyxsed.postscan.features.changegroup.presentation

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
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.usecase.AddPostUseCase
import ru.nyxsed.postscan.core.domain.usecase.DeleteGroupPostsUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetPostsForGroupDateIntervalUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetResourceUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.core.domain.usecase.UpdateGroupUseCase
import ru.nyxsed.postscan.core.domain.util.NotificationHelper
import ru.nyxsed.postscan.core.event.UiEvent
import ru.nyxsed.postscan.core.util.Constants.VK_URL
import ru.nyxsed.postscan.core.util.Constants.toDateLong
import ru.nyxsed.postscan.core.util.Constants.toStringDate

class ChangeGroupViewModel(
    private val group: Group,
    private val getResourceUseCase: GetResourceUseCase,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val getPostsForGroupDateIntervalUseCase: GetPostsForGroupDateIntervalUseCase,
    private val addPostUseCase: AddPostUseCase,
    private val deleteGroupPostsUseCase: DeleteGroupPostsUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val notificationHelper: NotificationHelper,
) : ViewModel() {

    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _state = MutableStateFlow(ChangeGroupState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    groupId = group.groupId,
                    groupName = group.name,
                    screenName = group.screenName,
                    avatarUrl = group.avatarUrl,
                    lastFetchDate = group.lastFetchDate.toStringDate().replace(".", ""),
                )
            }
        }
    }

    fun processIntent(changeGroupIntent: ChangeGroupIntent) {
        when (changeGroupIntent) {
            ChangeGroupIntent.ToggleDeleteDialog -> _state.update { it.copy(showDeleteDialog = !it.showDeleteDialog) }
            ChangeGroupIntent.ToggleDownloadDialog -> _state.update { it.copy(showDownloadDialog = !it.showDownloadDialog) }
            is ChangeGroupIntent.ChangeGroupName -> _state.update { it.copy(groupName = changeGroupIntent.value) }
            is ChangeGroupIntent.ChangeLastFetchDate -> _state.update { it.copy(lastFetchDate = changeGroupIntent.value) }
            ChangeGroupIntent.UpdateGroup -> updateGroup()
            ChangeGroupIntent.OpenGroupUri -> openGroupUri()
            is ChangeGroupIntent.LoadPosts -> loadPosts(changeGroupIntent.startDate, changeGroupIntent.endDate)
            ChangeGroupIntent.DeleteGroupPosts -> deleteGroupPosts()
        }
    }

    private fun updateGroup() {
        viewModelScope.launch {
            _state.value.let {
                val group = Group(
                    groupId = it.groupId,
                    name = it.groupName,
                    screenName = it.screenName,
                    avatarUrl = it.avatarUrl,
                    lastFetchDate = it.lastFetchDate.toDateLong()
                )
            }
            updateGroupUseCase(group)
            _uiEventFlow.emit(UiEvent.NavigateBack())
        }
    }

    private fun openGroupUri() {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.no_internet_connection)))
                return@launch
            }

            _uiEventFlow.emit(UiEvent.OpenUrl(url = "${VK_URL}${group.screenName}"))
        }
    }

    private fun loadPosts(startDate: String, endDate: String) {
        val startDateUnix = startDate.toDateLong()
        val endDateUnix = endDate.toDateLong()

        viewModelScope.launch {
            notificationHelper.initNotification()
            _state.update { it.copy(showCircularIndicator = true) }
            try {
                val postEntities = getPostsForGroupDateIntervalUseCase(
                    group = group,
                    startDate = startDateUnix,
                    endDate = endDateUnix + 86399000
                )
                postEntities.forEach { post ->
                    addPostUseCase(post)
                }
                notificationHelper.completeNotification()
                _state.update { it.copy(showCircularIndicator = false) }
            } catch (e: Exception) {
                _uiEventFlow.emit(UiEvent.ShowToast(e.message!!))
                notificationHelper.errorNotification(e.message!!)
                _state.update { it.copy(showCircularIndicator = false) }
            }
        }
        _state.update { it.copy(showDownloadDialog = !it.showDownloadDialog) }
    }

    private fun deleteGroupPosts() {
        viewModelScope.launch {
            deleteGroupPostsUseCase(group)
        }
        _state.update { it.copy(showDeleteDialog = !it.showDeleteDialog) }
    }
}