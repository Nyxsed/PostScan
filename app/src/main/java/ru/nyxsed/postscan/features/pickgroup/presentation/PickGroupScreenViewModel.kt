package ru.nyxsed.postscan.features.pickgroup.presentation

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
import ru.nyxsed.postscan.common.domain.usecase.DeleteGroupPostsUseCase
import ru.nyxsed.postscan.common.domain.usecase.DeleteGroupUseCase
import ru.nyxsed.postscan.common.domain.usecase.GetAllGroupsUseCase
import ru.nyxsed.postscan.common.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.common.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.common.domain.util.CustomResourcesProvider
import ru.nyxsed.postscan.common.util.UiEvent
import ru.nyxsed.postscan.features.login.presentation.LoginScreen
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.AddGroupUseCase
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.GetGroupsUseCase
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.SearchGroupsUseCase

class PickGroupScreenViewModel(
    private val customResourceProvider: CustomResourcesProvider,
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

    private val fetchedGroupsState = MutableStateFlow<List<GroupEntity>>(emptyList())

    private val existingGroupsSate: StateFlow<List<GroupEntity>> = getAllGroupsUseCase()

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setMode(mode: String) {
        when (mode) {
            "USER_GROUPS" -> {
                viewModelScope.launch {
                    getGroupsUseCase()
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
                _uiEventFlow.emit(UiEvent.ShowToast(customResourceProvider.getString(R.string.no_internet_connection)))
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

    fun addGroup(group: GroupEntity) {
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

    private var groupToDelete: GroupEntity? = null

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    fun toggleDeleteDialog(group: GroupEntity? = null) {
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