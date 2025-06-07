package ru.nyxsed.postscan.features.login.presentation

sealed class LoginIntent {
    object NavigateBack : LoginIntent()
    data class ShowError(val errorDesc: String) : LoginIntent()
}