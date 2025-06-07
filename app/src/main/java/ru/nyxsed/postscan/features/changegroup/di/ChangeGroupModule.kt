package ru.nyxsed.postscan.features.changegroup.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.features.changegroup.presentation.ChangeGroupViewModel

val changeGroupModule = module {
    viewModel {
        ChangeGroupViewModel(
            getResourceUseCase = get(),
            isInternetAvailableUseCase = get(),
            getPostsForGroupDateIntervalUseCase = get(),
            addPostUseCase = get(),
            deleteGroupPostsUseCase = get(),
            updateGroupUseCase = get(),
            notificationHelper = get(),
        )
    }
}