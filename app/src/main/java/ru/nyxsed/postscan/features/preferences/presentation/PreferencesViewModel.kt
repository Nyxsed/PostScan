package ru.nyxsed.postscan.features.preferences.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.id.VKID
import com.vk.id.logout.VKIDLogoutCallback
import com.vk.id.logout.VKIDLogoutFail
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.usecase.GetResourceUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetSettingBooleanUseCase
import ru.nyxsed.postscan.core.domain.usecase.SetSettingBooleanUseCase
import ru.nyxsed.postscan.core.event.UiEvent
import ru.nyxsed.postscan.features.preferences.domain.usecase.ExportDbUseCase
import ru.nyxsed.postscan.features.preferences.domain.usecase.ImportDbUseCase

class PreferencesViewModel(
    private val getSettingBooleanUseCase: GetSettingBooleanUseCase,
    private val setSettingBooleanUseCase: SetSettingBooleanUseCase,
    private val exportDbUseCase: ExportDbUseCase,
    private val importDbUseCase: ImportDbUseCase,
    private val getResourceUseCase: GetResourceUseCase,
) : ViewModel() {

    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private val _state = MutableStateFlow(PreferencesState())
    val state: StateFlow<PreferencesState> = _state.asStateFlow()

    fun processIntent(intent: PreferencesIntent) {
        when (intent) {
            is PreferencesIntent.ExportDB -> exportDataBaseToFile(intent.uri)
            is PreferencesIntent.ImportDB -> importDataBaseFromFile(intent.uri)
            PreferencesIntent.LoadSettings -> loadSettings()
            PreferencesIntent.Logout -> logOut()
            PreferencesIntent.ResetTutorial -> resetTutorials()
            is PreferencesIntent.ToggleSetting -> toggleSetting(intent.key, intent.value)
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    notLoadLikedPosts = getSettingBooleanUseCase(SettingKey.NOT_LOAD_LIKED_POSTS),
                    useMihon = getSettingBooleanUseCase(SettingKey.USE_MIHON),
                    deleteAfterLike = getSettingBooleanUseCase(SettingKey.DELETE_AFTER_LIKE)
                )
            }
        }
    }

    private fun toggleSetting(key: SettingKey, value: Boolean) {
        viewModelScope.launch {
            setSettingBooleanUseCase(key, value)
            loadSettings()
        }
    }

    private fun logOut() {
        viewModelScope.launch {
            VKID.instance.logout(object : VKIDLogoutCallback {
                override fun onSuccess() {
                    viewModelScope.launch {
                        _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.log_out_message)))
                    }
                }

                override fun onFail(fail: VKIDLogoutFail) {
                    viewModelScope.launch {
                        _uiEventFlow.emit(UiEvent.ShowToast(fail.description))
                    }
                }
            })

        }
    }

    private fun exportDataBaseToFile(uri: Uri) {
        viewModelScope.launch {
            val result = exportDbUseCase(uri)
            if (result) _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.export_db_message)))
        }
    }

    private fun importDataBaseFromFile(uri: Uri) {
        viewModelScope.launch {
            val result = importDbUseCase(uri)
            if (result) _uiEventFlow.emit(UiEvent.ShowToast(getResourceUseCase(R.string.import_db_message)))
        }
    }

    private fun resetTutorials() {
        viewModelScope.launch {
            setSettingBooleanUseCase(SettingKey.SHOWED_TUTORIAL_POSTS, false)
            setSettingBooleanUseCase(SettingKey.SHOWED_TUTORIAL_GROUPS, false)
            setSettingBooleanUseCase(SettingKey.SHOWED_TUTORIAL_IMAGE, false)
        }
    }
}