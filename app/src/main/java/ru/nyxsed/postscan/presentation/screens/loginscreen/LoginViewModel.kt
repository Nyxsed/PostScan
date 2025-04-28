package ru.nyxsed.postscan.presentation.screens.loginscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.nyxsed.postscan.util.UiEvent

class LoginViewModel() : ViewModel() {
    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    fun showLoginError(failDescription: String) {
        viewModelScope.launch {
            _uiEventFlow.emit(UiEvent.ShowToast(failDescription))
        }
    }
}