package ru.nyxsed.postscan.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.core.event.UiEvent

class LoginViewModel() : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    fun processIntent(intent: LoginIntent) {
        when(intent) {
            LoginIntent.NavigateBack -> {
                navigateBack()
            }

            is LoginIntent.ShowError -> {
                showLoginError(intent.errorDesc)
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEventFlow.emit(UiEvent.NavigateBack())
        }
    }

    private fun showLoginError(failDescription: String) {
        viewModelScope.launch {
            _uiEventFlow.emit(UiEvent.ShowToast(failDescription))
        }
    }
}