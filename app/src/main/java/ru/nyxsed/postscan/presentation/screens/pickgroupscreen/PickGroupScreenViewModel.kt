package ru.nyxsed.postscan.presentation.screens.pickgroupscreen

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.data.models.entity.GroupEntity
import ru.nyxsed.postscan.data.repository.DbRepository
import ru.nyxsed.postscan.data.repository.VkRepository
import ru.nyxsed.postscan.presentation.screens.loginscreen.LoginScreen
import ru.nyxsed.postscan.presentation.screens.pickgroupscreen.PickGroupState.*
import ru.nyxsed.postscan.util.ConnectionChecker
import ru.nyxsed.postscan.util.UiEvent
import kotlin.collections.filter

class PickGroupScreenViewModel(
    private val dbRepository: DbRepository,
    private val vkRepository: VkRepository,
    private val connectionChecker: ConnectionChecker,
    private val resources: Resources,
) : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _screenStateFlow = MutableStateFlow<PickGroupState>(PickGroupState.User())
    val screenStateFlow: StateFlow<PickGroupState> = _screenStateFlow.asStateFlow()

    private val fetchedGroupsState = MutableStateFlow<List<GroupEntity>>(emptyList())
    private val filteredGroupsState: StateFlow<List<GroupEntity>> = combine(
        fetchedGroupsState,
        dbRepository.getAllGroups()
    ) { fetchedGroups, dbGroups ->
        fetchedGroups.filter { group ->
            dbGroups.none { it.groupId == group.groupId }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery : StateFlow<String> = _searchQuery.asStateFlow()

    fun setMode(mode: String) {
        when (mode) {
            "USER_GROUPS" -> {
                viewModelScope.launch {
                    vkRepository.getGroupsStateFlow()
                        .collect {
                            fetchedGroupsState.value = it
                            _screenStateFlow.value = PickGroupState.User(filteredGroupsState.value)
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
                fetchedGroupsState.value = groups
            } catch (e: Exception) {
                _uiEventFlow.emit(UiEvent.ShowToast(e.message!!))
            }

            _screenStateFlow.value = PickGroupState.Search(groups = filteredGroupsState.value)
        }
    }

    fun addGroup(group: GroupEntity) {
        viewModelScope.launch {
            dbRepository.addGroup(group)
            val currentState = _screenStateFlow.value
            fetchedGroupsState.value = fetchedGroupsState.value.filter { it.groupId != group.groupId }

            when (currentState) {
                is PickGroupState.Search -> _screenStateFlow.value =
                    PickGroupState.Search(groups = filteredGroupsState.value)

                is PickGroupState.User -> _screenStateFlow.value = PickGroupState.User(groups = filteredGroupsState.value)
                else -> {}
            }
        }
    }

    fun changeSearchQuery(value: String) {
        _searchQuery.value = value
    }
}