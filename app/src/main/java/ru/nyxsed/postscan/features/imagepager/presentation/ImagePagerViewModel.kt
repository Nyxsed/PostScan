package ru.nyxsed.postscan.features.imagepager.presentation

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
import ru.nyxsed.postscan.common.domain.usecase.GetSettingBooleanUseCase
import ru.nyxsed.postscan.common.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.common.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.common.domain.usecase.SetSettingBooleanUseCase
import ru.nyxsed.postscan.common.domain.util.CustomResourcesProvider
import ru.nyxsed.postscan.common.util.Constants.VK_PHOTO_URL
import ru.nyxsed.postscan.common.util.UiEvent
import ru.nyxsed.postscan.features.imagepager.domain.usecase.ChangeContentLikeStatusUseCase
import ru.nyxsed.postscan.features.imagepager.domain.usecase.CheckContentLikeStatusUseCase
import ru.nyxsed.postscan.features.login.presentation.LoginScreen
import java.net.URLEncoder

class ImagePagerViewModel(
    private val customResourceProvider: CustomResourcesProvider,
    private val getSettingBooleanUseCase: GetSettingBooleanUseCase,
    private val setSettingBooleanUseCase: SetSettingBooleanUseCase,
    private val checkContentLikeStatusUseCase: CheckContentLikeStatusUseCase,
    private val changeContentLikeStatusUseCase: ChangeContentLikeStatusUseCase,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    private val isTokenValidUseCase: IsTokenValidUseCase,
) : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    fun changeLikeStatus(contentEntity: ContentEntity) {
        viewModelScope.launch {
            try {
                changeContentLikeStatusUseCase(contentEntity)
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
        return checkContentLikeStatusUseCase(contentEntity)
    }

    fun openPostUri(uriHandler: UriHandler, contentEntity: ContentEntity) {
        uriHandler.openUri("${VK_PHOTO_URL}${contentEntity.ownerId}_${contentEntity.contentId}")
    }

    fun findImage(uriHandler: UriHandler, link: String, source: String) {
        val endLink = URLEncoder.encode(link, "utf-8")
        uriHandler.openUri("$source$endLink")
    }

    suspend fun checkConnect(): Boolean {
        if (!isInternetAvailableUseCase()) {
            _uiEventFlow.emit(UiEvent.ShowToast(customResourceProvider.getString(R.string.no_internet_connection)))
            return false
        }

        if (!isTokenValidUseCase()) {
            _uiEventFlow.emit(UiEvent.ShowToast(customResourceProvider.getString(R.string.token_is_invalid)))
            return false
        }
        return true
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