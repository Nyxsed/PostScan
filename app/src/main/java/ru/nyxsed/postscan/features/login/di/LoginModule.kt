package ru.nyxsed.postscan.features.login.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.features.login.presentation.LoginViewModel

val loginModule = module {
    viewModel {
        LoginViewModel()
    }
}