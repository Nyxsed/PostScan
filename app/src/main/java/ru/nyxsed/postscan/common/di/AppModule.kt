package ru.nyxsed.postscan.common.di


import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.common.presentation.screens.changegroupscreen.ChangeGroupScreenViewModel
import ru.nyxsed.postscan.common.presentation.screens.postsscreen.PostsScreenViewModel

val appModule = module {
    // viewmodels
    viewModel<PostsScreenViewModel> {
        PostsScreenViewModel(
            dbRepository = get(),
            vkRepository = get(),
            dataStoreRepository = get(),
            connectionChecker = get(),
            resources = get()
        )
    }

    viewModel<ChangeGroupScreenViewModel> {
        ChangeGroupScreenViewModel(
            vkRepository = get(),
            dbRepository = get(),
            connectionChecker = get(),
            resources = get()
        )
    }
}