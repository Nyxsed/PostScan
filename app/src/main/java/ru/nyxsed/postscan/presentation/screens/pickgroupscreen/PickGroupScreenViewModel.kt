package ru.nyxsed.postscan.presentation.screens.pickgroupscreen

import android.content.res.Resources
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
import ru.nyxsed.postscan.features.login.presentation.screens.loginscreen.LoginScreen
import ru.nyxsed.postscan.presentation.screens.pickgroupscreen.PickGroupState.*
import ru.nyxsed.postscan.util.ConnectionChecker
import ru.nyxsed.postscan.util.UiEvent

class PickGroupScreenViewModel(
    private val dbRepository: DbRepository,
    private val vkRepository: VkRepository,
    private val connectionChecker: ConnectionChecker,
    private val resources: Resources,
) : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _screenStateFlow = MutableStateFlow<PickGroupState>(PickGroupState.User())
    val screenStateFlow: StateFlow<PickGroupState> = _screenStateFlow.asStateFlow()

    private val fetchedGroupsState = MutableStateFlow<List<GroupEntity>>(emptyList())

    private val existingGroupsSate: StateFlow<List<GroupEntity>> = dbRepository.getAllGroups()

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setMode(mode: String) {
        when (mode) {
            "USER_GROUPS" -> {
                viewModelScope.launch {
                    vkRepository.getGroupsStateFlow()
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
            if (!connectionChecker.isInternetAvailable()) {
                _uiEventFlow.emit(UiEvent.ShowToast(resources.getString(R.string.no_internet_connection)))
                return@launch
            }

            if (!connectionChecker.isTokenValid()) {
                _uiEventFlow.emit(UiEvent.Navigate(LoginScreen))
                return@launch
            }

            _screenStateFlow.value = PickGroupState.Loading
            try {
                val groups = vkRepository.searchGroups(searchQuery)
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
            dbRepository.addGroup(group)
            dbRepository.getAllGroups().collect {
                when (_screenStateFlow.value) {
                    is Search -> _screenStateFlow.value =
                        Search(groups = fetchedGroupsState.value, existingGroups = it)

                    is User -> _screenStateFlow.value =
                        User(groups = fetchedGroupsState.value, existingGroups = it)

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
            dbRepository.deleteGroup(groupToDelete!!)
            dbRepository.deleteAllPostsForGroup(groupToDelete!!)
            toggleDeleteDialog()
            dbRepository.getAllGroups().collect {
                when (_screenStateFlow.value) {
                    is Search -> _screenStateFlow.value =
                        Search(groups = fetchedGroupsState.value, existingGroups = it)

                    is User -> _screenStateFlow.value =
                        User(groups = fetchedGroupsState.value, existingGroups = it)

                    else -> {}
                }
            }
        }
    }
}