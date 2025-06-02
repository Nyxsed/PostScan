package ru.nyxsed.postscan.features.groups.presentation

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
import ru.nyxsed.postscan.common.domain.models.SettingKey
import ru.nyxsed.postscan.common.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.common.domain.usecase.AddPostUseCase
import ru.nyxsed.postscan.common.domain.usecase.DeleteGroupPostsUseCase
import ru.nyxsed.postscan.common.domain.usecase.DeleteGroupUseCase
import ru.nyxsed.postscan.common.domain.usecase.GetAllGroupsUseCase
import ru.nyxsed.postscan.common.domain.usecase.GetPostsForGroupDateIntervalUseCase
import ru.nyxsed.postscan.common.domain.usecase.GetSettingBooleanUseCase
import ru.nyxsed.postscan.common.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.common.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.common.domain.usecase.SetSettingBooleanUseCase
import ru.nyxsed.postscan.common.domain.util.CustomResourcesProvider
import ru.nyxsed.postscan.common.util.Constants.toDateLong
import ru.nyxsed.postscan.common.util.UiEvent
import ru.nyxsed.postscan.features.changegroup.presentation.ChangeGroupScreen
import ru.nyxsed.postscan.features.groups.domain.usecase.DeleteAllPostsUseCase
import ru.nyxsed.postscan.features.login.presentation.LoginScreen
import ru.nyxsed.postscan.features.pickgroup.presentation.PickGroupScreen

class GroupsScreenViewModel(
    private val customResourceProvider: CustomResourcesProvider,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val isTokenValidUseCase: IsTokenValidUseCase,
    private val getSettingBooleanUseCase: GetSettingBooleanUseCase,
    private val setSettingBooleanUseCase: SetSettingBooleanUseCase,
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val deleteGroupPostsUseCase: DeleteGroupPostsUseCase,
    private val deleteAllPostsUseCase: DeleteAllPostsUseCase,
    private val addPostUseCase: AddPostUseCase,
    private val getPostsForGroupDateIntervalUseCase: GetPostsForGroupDateIntervalUseCase,
) : ViewModel() {
    val dbGroups = getAllGroupsUseCase()
    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _showDeleteAllDialog = MutableStateFlow(false)
    val showDeleteAllDialog: StateFlow<Boolean> = _showDeleteAllDialog.asStateFlow()

    private val _showDownloadDialog = MutableStateFlow(false)
    val showDownloadDialog: StateFlow<Boolean> = _showDownloadDialog.asStateFlow()

    private val _showTutorial = MutableStateFlow(false)
    val showTutorial: StateFlow<Boolean> = _showTutorial.asStateFlow()

    private val _showCircularIndicator = MutableStateFlow(false)
    val showCircularIndicator: StateFlow<Boolean> = _showCircularIndicator.asStateFlow()

    private var groupToDelete: GroupEntity? = null

    fun deleteGroupWithPosts() {
        viewModelScope.launch {
            deleteGroupUseCase(groupToDelete!!)
            deleteGroupPostsUseCase(groupToDelete!!)
            toggleDeleteDialog()
        }
    }

    fun navigateToPickScreen(param: String) {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(UiEvent.ShowToast(customResourceProvider.getString(R.string.no_internet_connection)))
                return@launch
            }

            if (!isTokenValidUseCase()) {
                _uiEventFlow.emit(UiEvent.Navigate(LoginScreen))
                return@launch
            }

            _uiEventFlow.emit(UiEvent.NavigateToPicker(PickGroupScreen, param))
            toggleAddDialog()
        }
    }

    fun navigateToChangeGroupScreen(param: GroupEntity) {
        viewModelScope.launch {
            _uiEventFlow.emit(UiEvent.NavigateToChangeGroup(ChangeGroupScreen, param))
        }
    }

    fun toggleAddDialog() {
        _showAddDialog.value = !_showAddDialog.value
    }

    fun toggleDeleteDialog(group: GroupEntity? = null) {
        _showDeleteDialog.value = !_showDeleteDialog.value
        groupToDelete = group
    }

    fun toggleDeleteAllDialog() {
        _showDeleteAllDialog.value = !_showDeleteAllDialog.value
    }

    fun deleteAllPosts() {
        viewModelScope.launch {
            deleteAllPostsUseCase()
            toggleDeleteAllDialog()
        }
    }

    fun toggleDownloadDialog() {
        _showDownloadDialog.value = !_showDownloadDialog.value
    }

    fun loadPosts(startDate: String, endDate: String) {
        val startDateUnix = startDate.toDateLong()
        val endDateUnix = endDate.toDateLong()

        viewModelScope.launch {
            _uiEventFlow.emit(UiEvent.InitNotification())
            _showCircularIndicator.value = true
            try {
                dbGroups.value.forEachIndexed { index, group ->
                    val postEntities = getPostsForGroupDateIntervalUseCase(
                        groupEntity = group,
                        startDate = startDateUnix,
                        endDate = endDateUnix + 86399000
                    )
                    postEntities.forEach { post ->
                        addPostUseCase(post)
                    }

                    val percentage = (index + 1) * 100 / dbGroups.value.size
                    _uiEventFlow.emit(UiEvent.UpdateNotification(percentage))

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

    fun showTutorial() {
        viewModelScope.launch {
            val setting = getSettingBoolean(SettingKey.SHOWED_TUTORIAL_GROUPS)
            _showTutorial.value = setting
        }
    }

    suspend fun getSettingBoolean(key: SettingKey): Boolean {
        return getSettingBooleanUseCase(key)
    }

    fun setSettingBoolean(key: SettingKey, value: Boolean) {
        viewModelScope.launch {
            setSettingBooleanUseCase(key, value)
        }
    }
}