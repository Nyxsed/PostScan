package ru.nyxsed.postscan.features.changegroup.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.features.changegroup.presentation.ChangeGroupScreenViewModel

val changeGroupModule = module {
    viewModel {
        ChangeGroupScreenViewModel(
            customResourceProvider = get(),
            isInternetAvailableUseCase = get(),
            getPostsForGroupDateIntervalUseCase = get(),
            addPostUseCase = get(),
            deleteGroupPostsUseCase = get(),
            updateGroupUseCase = get(),
        )
    }
}