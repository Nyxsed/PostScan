package ru.nyxsed.postscan.features.pickgroup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.PickGroupMode
import ru.nyxsed.postscan.core.domain.usecase.DeleteGroupPostsUseCase
import ru.nyxsed.postscan.core.domain.usecase.DeleteGroupUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetAllGroupsUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetResourceUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.core.event.UiEvent
import ru.nyxsed.postscan.features.login.presentation.LoginScreen
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.AddGroupUseCase
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.GetUserGroupsUseCase
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.SearchGroupsUseCase

class PickGroupViewModel(
    private val getResourceUseCase: GetResourceUseCase,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val isTokenValidUseCase: IsTokenValidUseCase,
    private val getUserGroupsUseCase: GetUserGroupsUseCase,
    private val searchGroupsUseCase: SearchGroupsUseCase,
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val addGroupUseCase: AddGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val deleteGroupPostsUseCase: DeleteGroupPostsUseCase,
    private val mode: PickGroupMode,
) : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _state = MutableStateFlow<PickGroupState>(
        PickGroupState(
            mode = mode
        )
    )
    val state: StateFlow<PickGroupState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getAllGroupsUseCase().collect { groups ->
                _state.update { it.copy(existingGroups = groups) }
            }
        }

        if (_state.value.mode == PickGroupMode.USER) {
            viewModelScope.launch {
                getUserGroupsUseCase().collect { groups ->
                    _state.update { it.copy(fetchedGroups = groups) }
                }
            }
        }
    }

    fun processIntent(intent: PickGroupIntent) {
        viewModelScope.launch {
            when (intent) {
                PickGroupIntent.DeleteGroupWithPosts -> deleteGroupWithPosts()
                PickGroupIntent.NavigateBack -> _uiEventFlow.emit(UiEvent.NavigateBack())
                is PickGroupIntent.ChangeSearchQuery -> _state.update { it.copy(searchQuery = intent.query) }
                is PickGroupIntent.ToggleDeleteDialog -> toggleDeleteDialog(intent.group)
                is PickGroupIntent.AddGroup -> addGroupUseCase(intent.group)
                is PickGroupIntent.FetchGroups -> fetchGroups(intent.query)
            }
        }
    }

    private fun fetchGroups(searchQuery: String) {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.no_internet_connection)))
                return@launch
            }

            if (!isTokenValidUseCase()) {
                _uiEventFlow.emit(UiEvent.NavigateTo(LoginScreen))
                return@launch
            }

            _state.update { it.copy(mode = PickGroupMode.LOADING) }

            try {
                val foundGroups = searchGroupsUseCase(searchQuery).distinctBy { it.groupId }
                _state.update { it.copy(fetchedGroups = foundGroups) }
            } catch (e: Exception) {
                _uiEventFlow.emit(UiEvent.ShowToast(e.message!!))
            }

            _state.update { it.copy(mode = PickGroupMode.SEARCH) }
        }
    }

    fun toggleDeleteDialog(group: Group? = null) {
        _state.update {
            it.copy(
                showDeleteDialog = !_state.value.showDeleteDialog,
                groupToDelete = group
            )
        }
    }

    private suspend fun deleteGroupWithPosts() {
        deleteGroupUseCase(_state.value.groupToDelete!!)
        deleteGroupPostsUseCase(_state.value.groupToDelete!!)
        toggleDeleteDialog()
    }
}