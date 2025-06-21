package ru.nyxsed.postscan.features.groups.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.PickGroupMode
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.usecase.AddPostUseCase
import ru.nyxsed.postscan.core.domain.usecase.DeleteGroupPostsUseCase
import ru.nyxsed.postscan.core.domain.usecase.DeleteGroupUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetAllGroupsUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetPostsForGroupDateIntervalUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetResourceUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetSettingBooleanUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.core.domain.usecase.SetSettingBooleanUseCase
import ru.nyxsed.postscan.core.domain.util.NotificationHelper
import ru.nyxsed.postscan.core.event.UiEvent
import ru.nyxsed.postscan.core.util.Constants.toDateLong
import ru.nyxsed.postscan.features.changegroup.presentation.ChangeGroupScreen
import ru.nyxsed.postscan.features.groups.domain.usecase.DeleteAllPostsUseCase
import ru.nyxsed.postscan.features.login.presentation.LoginScreen
import ru.nyxsed.postscan.features.pickgroup.presentation.PickGroupScreen

class GroupsViewModel(
    private val getResourceUseCase: GetResourceUseCase,
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
    private val notificationHelper: NotificationHelper,
) : ViewModel() {

    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _state = MutableStateFlow(GroupsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val setting = getSettingBooleanUseCase(SettingKey.SHOWED_TUTORIAL_GROUPS)

            getAllGroupsUseCase().collectLatest { groups ->
                _state.update { it.copy(showTutorial = setting, groups = groups) }
            }
        }
    }

    fun processIntent(intent: GroupsIntent) {
        viewModelScope.launch {
            when (intent) {
                GroupsIntent.DeleteGroupWithPosts -> deleteGroupWithPosts()
                GroupsIntent.GroupsTutorialCompleted -> setSettingBooleanUseCase(SettingKey.SHOWED_TUTORIAL_GROUPS, true)
                GroupsIntent.ToggleAddDialog -> toggleAddDialog()
                GroupsIntent.ToggleDownloadDialog -> toggleDownloadDialog()
                GroupsIntent.ToggleDeleteAllDialog -> toggleDeleteAllDialog()
                is GroupsIntent.ToggleDeleteDialog -> toggleDeleteDialog(intent.group)
                is GroupsIntent.NavigateToChangeGroupScreen -> navigateToChangeGroupScreen(intent.group)
                is GroupsIntent.NavigateToPickScreen -> navigateToPickScreen(intent.dest)
                GroupsIntent.DeleteAllPosts -> deleteAllPosts()
                is GroupsIntent.LoadPosts -> loadPosts(intent.startDate, intent.endDate)
            }
        }
    }

    private fun deleteGroupWithPosts() {
        viewModelScope.launch {
            deleteGroupUseCase(_state.value.groupToDelete!!)
            deleteGroupPostsUseCase(_state.value.groupToDelete!!)
            toggleDeleteDialog()
        }
    }

    private fun navigateToPickScreen(param: PickGroupMode) {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.no_internet_connection)))
                return@launch
            }

            if (!isTokenValidUseCase()) {
                _uiEventFlow.emit(UiEvent.NavigateTo(LoginScreen))
                return@launch
            }

            _uiEventFlow.emit(UiEvent.NavigateTo(PickGroupScreen, param))
            toggleAddDialog()
        }
    }

    private fun navigateToChangeGroupScreen(param: Group) {
        viewModelScope.launch {
            _uiEventFlow.emit(UiEvent.NavigateTo(ChangeGroupScreen, param))
        }
    }

    private fun toggleAddDialog() {
        _state.update { it.copy(showAddDialog = !it.showAddDialog) }
    }

    private fun toggleDeleteDialog(group: Group? = null) {
        _state.update {
            it.copy(
                showDeleteDialog = !it.showDeleteDialog,
                groupToDelete = group,
            )
        }
    }

    private fun toggleDownloadDialog() {
        _state.update { it.copy(showDownloadDialog = !it.showDownloadDialog) }
    }

    private fun toggleDeleteAllDialog() {
        _state.update { it.copy(showDeleteAllDialog = !it.showDeleteAllDialog) }
    }

    private fun deleteAllPosts() {
        viewModelScope.launch {
            deleteAllPostsUseCase()
            toggleDeleteAllDialog()
        }
    }

    private fun loadPosts(startDate: String, endDate: String) {
        val startDateUnix = startDate.toDateLong()
        val endDateUnix = endDate.toDateLong()

        viewModelScope.launch {
            notificationHelper.initNotification()
            _state.update { it.copy(showCircularIndication = true) }
            try {
                state.value.groups.forEachIndexed { index, group ->
                    val postEntities = getPostsForGroupDateIntervalUseCase(
                        group = group,
                        startDate = startDateUnix,
                        endDate = endDateUnix + 86399000
                    )
                    postEntities.forEach { post ->
                        addPostUseCase(post)
                    }

                    val percentage = (index + 1) * 100 / state.value.groups.size
                    notificationHelper.updateProgressNotification(percentage)

                }
                notificationHelper.completeNotification()
                _state.update { it.copy(showCircularIndication = false) }
            } catch (e: Exception) {
                _uiEventFlow.emit(UiEvent.ShowToast(e.message!!))
                notificationHelper.errorNotification(e.message!!)
                _state.update { it.copy(showCircularIndication = false) }
            }
        }
        toggleDownloadDialog()
    }
}