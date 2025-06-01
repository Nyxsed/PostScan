package ru.nyxsed.postscan.common.presentation.screens.changegroupscreen

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
import ru.nyxsed.postscan.common.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.common.domain.repository.DbRepository
import ru.nyxsed.postscan.common.domain.repository.VkRepository
import ru.nyxsed.postscan.common.domain.util.ConnectionChecker
import ru.nyxsed.postscan.common.domain.util.CustomResourcesProvider
import ru.nyxsed.postscan.common.util.Constants.VK_URL
import ru.nyxsed.postscan.common.util.Constants.toDateLong
import ru.nyxsed.postscan.common.util.UiEvent

class ChangeGroupScreenViewModel(
    private val vkRepository: VkRepository,
    private val dbRepository: DbRepository,
    private val connectionChecker: ConnectionChecker,
    private val resources: CustomResourcesProvider,
) : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _groupId = MutableStateFlow<Long>(0)
    val groupId : StateFlow<Long> = _groupId.asStateFlow()

    private val _groupName = MutableStateFlow<String>("")
    val groupName : StateFlow<String> = _groupName.asStateFlow()

    private val _screenName = MutableStateFlow<String>("")
    val screenName : StateFlow<String> = _screenName.asStateFlow()

    private val _avatarUrl = MutableStateFlow<String>("")
    val avatarUrl : StateFlow<String> = _avatarUrl.asStateFlow()

    private val _lastFetchDate = MutableStateFlow<String>("")
    val lastFetchDate : StateFlow<String> = _lastFetchDate.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _showDownloadDialog = MutableStateFlow(false)
    val showDownloadDialog: StateFlow<Boolean> = _showDownloadDialog.asStateFlow()

    private val _showCircularIndicator = MutableStateFlow(false)
    val showCircularIndicator: StateFlow<Boolean> = _showCircularIndicator.asStateFlow()

    fun toggleDeleteDialog() {
        _showDeleteDialog.value = !_showDeleteDialog.value
    }

    fun toggleDownloadDialog() {
        _showDownloadDialog.value = !_showDownloadDialog.value
    }

    fun changeGroupId(value: Long) {
        _groupId.value = value
    }

    fun changeGroupName(value: String) {
        _groupName.value = value
    }

    fun changeScreenName(value: String) {
        _screenName.value = value
    }

    fun changeAvatarUrl(value: String) {
        _avatarUrl.value = value
    }

    fun changeLastFetchDate(value: String) {
        _lastFetchDate.value = value
    }

    val regex = Regex("^([0-2][0-9]|3[01])(0[1-9]|1[0-2])[0-9]{4}$")

    fun updateGroup(
        groupId: Long,
        groupName: String,
        screenName: String,
        avatarUrl: String,
        lastFetchDate: String,
    ) {
        viewModelScope.launch {
            val fetchDate = lastFetchDate.toDateLong()

            val group = GroupEntity(
                groupId = groupId,
                name = groupName,
                screenName = screenName,
                avatarUrl = avatarUrl,
                lastFetchDate = fetchDate
            )

            dbRepository.updateGroup(group)
            _uiEventFlow.emit(UiEvent.NavigateBack())
        }
    }

    fun openGroupUri(group: GroupEntity) {
        viewModelScope.launch {
            if (!connectionChecker.isInternetAvailable()) {
                _uiEventFlow.emit(UiEvent.ShowToast(resources.getString(R.string.no_internet_connection)))
                return@launch
            }

            _uiEventFlow.emit(UiEvent.OpenUrl(url = "${VK_URL}${group.screenName}"))
        }
    }

    fun loadPosts(group: GroupEntity, startDate: String, endDate: String) {
        val startDateUnix = startDate.toDateLong()
        val endDateUnix = endDate.toDateLong()

        viewModelScope.launch {
            _uiEventFlow.emit(UiEvent.InitNotification())
            _showCircularIndicator.value = true
            try {
                val postEntities = vkRepository.getPostsForGroupDateInterval(
                    groupEntity = group,
                    startDate = startDateUnix,
                    endDate = endDateUnix + 86399000
                )
                postEntities.forEach { post ->
                    dbRepository.addPost(post)
                }
                _uiEventFlow.emit(UiEvent.CompleteNotification())
                _showCircularIndicator.value = false
            } catch (e: Exception) {
                _uiEventFlow.emit(UiEvent.ShowToast(e.message!!))
                _uiEventFlow.emit(UiEvent.ErrorNotification(e.message!!))
                _showCircularIndicator.value = false
            }
        }
        toggleDownloadDialog()
    }

    fun deleteGroupWithPosts(group: GroupEntity) {
        viewModelScope.launch {
            dbRepository.deleteAllPostsForGroup(group)
        }
        toggleDeleteDialog()
    }
}