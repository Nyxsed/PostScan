package ru.nyxsed.postscan.common.di


import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.common.presentation.screens.postsscreen.PostsScreenViewModel

val appModule = module {
    viewModel<PostsScreenViewModel> {
        PostsScreenViewModel(
            dbRepository = get(),
            vkRepository = get(),
            dataStoreRepository = get(),
            connectionChecker = get(),
            resources = get()
        )
    }
}