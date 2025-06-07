package ru.nyxsed.postscan.features.pickgroup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.usecase.DeleteGroupPostsUseCase
import ru.nyxsed.postscan.core.domain.usecase.DeleteGroupUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetAllGroupsUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetResourceUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.core.event.UiEvent
import ru.nyxsed.postscan.features.login.presentation.LoginScreen
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.AddGroupUseCase
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.GetGroupsUseCase
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.SearchGroupsUseCase

class PickGroupScreenViewModel(
    private val getResourceUseCase: GetResourceUseCase,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val isTokenValidUseCase: IsTokenValidUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val searchGroupsUseCase: SearchGroupsUseCase,
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val addGroupUseCase: AddGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val deleteGroupPostsUseCase: DeleteGroupPostsUseCase,
) : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _screenStateFlow = MutableStateFlow<PickGroupState>(PickGroupState.User())
    val screenStateFlow: StateFlow<PickGroupState> = _screenStateFlow.asStateFlow()

    private val fetchedGroupsState = MutableStateFlow<List<Group>>(emptyList())

    private val existingGroupsSate: StateFlow<List<Group>> = getAllGroupsUseCase()

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val groupsState = getGroupsUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setMode(mode: String) {
        when (mode) {
            "USER_GROUPS" -> {
                viewModelScope.launch {
                    groupsState
                        .collect {
                            fetchedGroupsState.value = it
                            _screenStateFlow.value = PickGroupState.User(
                                groups = fetchedGroupsState.value,
                                existingGroups = existingGroupsSate.value
                            )
                        }
                }
            }

            "SEARCH" -> {
                _screenStateFlow.value = PickGroupState.Search()
            }
        }
    }

    fun fetchedGroups(searchQuery: String) {
        viewModelScope.launch {
            if (!isInternetAvailableUseCase()) {
                _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.no_internet_connection)))
                return@launch
            }

            if (!isTokenValidUseCase()) {
                _uiEventFlow.emit(UiEvent.Navigate(LoginScreen))
                return@launch
            }

            _screenStateFlow.value = PickGroupState.Loading
            try {
                val groups = searchGroupsUseCase(searchQuery)
                fetchedGroupsState.value = groups.distinctBy { it.groupId }
            } catch (e: Exception) {
                _uiEventFlow.emit(UiEvent.ShowToast(e.message!!))
            }

            _screenStateFlow.value =
                PickGroupState.Search(groups = fetchedGroupsState.value, existingGroups = existingGroupsSate.value)
        }
    }

    fun addGroup(group: Group) {
        viewModelScope.launch {
            addGroupUseCase(group)
            getAllGroupsUseCase().collect {
                when (_screenStateFlow.value) {
                    is PickGroupState.Search -> _screenStateFlow.value =
                        PickGroupState.Search(groups = fetchedGroupsState.value, existingGroups = it)

                    is PickGroupState.User -> _screenStateFlow.value =
                        PickGroupState.User(groups = fetchedGroupsState.value, existingGroups = it)

                    else -> {}
                }
            }
        }
    }


    fun changeSearchQuery(value: String) {
        _searchQuery.value = value
    }

    fun navigateBack() {
        viewModelScope.launch {
            _uiEventFlow.emit(UiEvent.NavigateBack())
        }
    }

    private var groupToDelete: Group? = null

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    fun toggleDeleteDialog(group: Group? = null) {
        _showDeleteDialog.value = !_showDeleteDialog.value
        groupToDelete = group
    }

    fun deleteGroupWithPosts() {
        viewModelScope.launch {
            deleteGroupUseCase(groupToDelete!!)
            deleteGroupPostsUseCase(groupToDelete!!)
            toggleDeleteDialog()
            getAllGroupsUseCase().collect {
                when (_screenStateFlow.value) {
                    is PickGroupState.Search -> _screenStateFlow.value =
                        PickGroupState.Search(groups = fetchedGroupsState.value, existingGroups = it)

                    is PickGroupState.User -> _screenStateFlow.value =
                        PickGroupState.User(groups = fetchedGroupsState.value, existingGroups = it)

                    else -> {}
                }
            }
        }
    }
}