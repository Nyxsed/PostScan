package ru.nyxsed.postscan.common.presentation.screens.imagepagerscreen

import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.common.domain.models.SettingKey
import ru.nyxsed.postscan.common.domain.models.entity.ContentEntity
import ru.nyxsed.postscan.common.domain.repository.DataStoreRepository
import ru.nyxsed.postscan.common.domain.repository.VkRepository
import ru.nyxsed.postscan.common.domain.util.ConnectionChecker
import ru.nyxsed.postscan.common.domain.util.CustomResourcesProvider
import ru.nyxsed.postscan.common.util.Constants.VK_PHOTO_URL
import ru.nyxsed.postscan.common.util.UiEvent
import ru.nyxsed.postscan.features.login.presentation.LoginScreen

class ImagePagerViewModel(
    private val vkRepository: VkRepository,
    private val connectionChecker: ConnectionChecker,
    private val resources: CustomResourcesProvider,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    fun changeLikeStatus(contentEntity: ContentEntity) {
        viewModelScope.launch {
            try {
                vkRepository.changeLikeStatus(contentEntity)
            } catch (e: Exception) {
                _uiEventFlow.emit(UiEvent.ShowToast(e.message!!))
            }
        }

    }

    fun navigateToLogin() {
        viewModelScope.launch {
            _uiEventFlow.emit(UiEvent.Navigate(LoginScreen))
        }
    }

    suspend fun checkLikeStatus(contentEntity: ContentEntity): Boolean {
        return vkRepository.checkLikeStatus(contentEntity)
    }

    fun openPostUri(uriHandler: UriHandler, contentEntity: ContentEntity) {
        uriHandler.openUri("${VK_PHOTO_URL}${contentEntity.ownerId}_${contentEntity.contentId}")
    }

    fun findImage(uriHandler: UriHandler, link: String, source: String) {
        val endLink = java.net.URLEncoder.encode(link, "utf-8")
        uriHandler.openUri("$source$endLink")
    }

    suspend fun checkConnect(): Boolean {
        if (!connectionChecker.isInternetAvailable()) {
            _uiEventFlow.emit(UiEvent.ShowToast(resources.getString(R.string.no_internet_connection)))
            return false
        }

        if (!connectionChecker.isTokenValid()) {
            _uiEventFlow.emit(UiEvent.ShowToast(resources.getString(R.string.token_is_invalid)))
            return false
        }
        return true
    }

    suspend fun getSettingBoolean(key: SettingKey): Boolean {
        return dataStoreRepository.getBoolean(key)
    }

    fun setSettingBoolean(key: SettingKey, value: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.setBoolean(key, value)
        }
    }
}