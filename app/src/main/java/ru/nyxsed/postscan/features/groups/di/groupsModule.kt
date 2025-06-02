package ru.nyxsed.postscan.features.groups.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.features.groups.domain.usecase.DeleteAllPostsUseCase
import ru.nyxsed.postscan.features.groups.presentation.GroupsScreenViewModel

val groupsModule = module {
    factory { DeleteAllPostsUseCase(get()) }

    viewModel {
        GroupsScreenViewModel(
            customResourceProvider = get(),
            isInternetAvailableUseCase = get(),
            isTokenValidUseCase = get(),
            getSettingUseCase = get(),
            setSettingUseCase = get(),
            getAllGroupsUseCase = get(),
            deleteGroupUseCase = get(),
            deleteGroupPostsUseCase = get(),
            deleteAllPostsUseCase = get(),
            addPostUseCase = get(),
            getPostsForGroupDateIntervalUseCase = get(),
        )
    }
}