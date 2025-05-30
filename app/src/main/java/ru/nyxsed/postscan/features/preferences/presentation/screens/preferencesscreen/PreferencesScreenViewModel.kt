package ru.nyxsed.postscan.features.preferences.presentation.screens.preferencesscreen

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
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.features.preferences.domain.model.SettingKey
import ru.nyxsed.postscan.features.preferences.domain.usecase.ExportDbUseCase
import ru.nyxsed.postscan.features.preferences.domain.usecase.GetSettingUseCase
import ru.nyxsed.postscan.features.preferences.domain.usecase.ImportDbUseCase
import ru.nyxsed.postscan.features.preferences.domain.usecase.SetSettingUseCase
import ru.nyxsed.postscan.features.preferences.domain.util.CustomResourcesProvider
import ru.nyxsed.postscan.util.UiEvent

class PreferencesScreenViewModel(
    private val getSetting: GetSettingUseCase,
    private val setSetting: SetSettingUseCase,
    private val exportDb: ExportDbUseCase,
    private val importDb: ImportDbUseCase,
    private val resourcesProvider: CustomResourcesProvider,
) : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    private var _settingNotLoadLikedPosts = MutableStateFlow<Boolean>(false)
    val settingNotLoadLikedPosts: StateFlow<Boolean> = _settingNotLoadLikedPosts.asStateFlow()

    private var _settingUseMihon = MutableStateFlow<Boolean>(false)
    val settingUseMihon: StateFlow<Boolean> = _settingUseMihon.asStateFlow()

    private var _settingDeleteAfterLike = MutableStateFlow<Boolean>(false)
    val settingDeleteAfterLike: StateFlow<Boolean> = _settingDeleteAfterLike.asStateFlow()

    fun saveSettingBoolean(key: SettingKey, value: Boolean) {
        viewModelScope.launch {
            setSetting(key, value)
            when (key) {
                SettingKey.NOT_LOAD_LIKED_POSTS -> _settingNotLoadLikedPosts.value = !_settingNotLoadLikedPosts.value
                SettingKey.USE_MIHON -> _settingUseMihon.value = !_settingUseMihon.value
                SettingKey.DELETE_AFTER_LIKE -> _settingDeleteAfterLike.value = !_settingDeleteAfterLike.value
                else -> {}
            }
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            _settingNotLoadLikedPosts.value = getSetting(SettingKey.NOT_LOAD_LIKED_POSTS)
            _settingUseMihon.value = getSetting(SettingKey.USE_MIHON)
            _settingDeleteAfterLike.value = getSetting(SettingKey.DELETE_AFTER_LIKE)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            VKID.instance.logout(object : VKIDLogoutCallback {
                override fun onSuccess() {
                    TODO("Not yet implemented")
                }

                override fun onFail(fail: VKIDLogoutFail) {
                    TODO("Not yet implemented")
                }
            })
            _uiEventFlow.emit(UiEvent.ShowToast(resourcesProvider.getString(R.string.log_out_message)))
        }
    }

    fun exportDataBaseToFile(uri: Uri) {
        viewModelScope.launch {
            val result = exportDb(uri)
            if (result) {
                _uiEventFlow.emit(UiEvent.ShowToast(resourcesProvider.getString(R.string.export_db_message)))
            }
        }
    }

    fun importDataBaseFromFile(uri: Uri) {
        viewModelScope.launch {
            val result = importDb(uri)
            if (result) {
                _uiEventFlow.emit(UiEvent.ShowToast(resourcesProvider.getString(R.string.import_db_message)))
            }
        }
    }
}