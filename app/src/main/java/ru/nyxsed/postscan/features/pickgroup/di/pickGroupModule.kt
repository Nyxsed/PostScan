package ru.nyxsed.postscan.features.pickgroup.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.AddGroupUseCase
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.GetGroupsUseCase
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.SearchGroupsUseCase
import ru.nyxsed.postscan.features.pickgroup.presentation.PickGroupScreenViewModel

val pickGroupModule = module {
    factory { GetGroupsUseCase(get()) }
    factory { SearchGroupsUseCase(get(), get()) }
    factory { AddGroupUseCase(get()) }

    viewModel {
        PickGroupScreenViewModel(
            getResourceUseCase = get(),
            isInternetAvailableUseCase = get(),
            isTokenValidUseCase = get(),
            getGroupsUseCase = get(),
            searchGroupsUseCase = get(),
            getAllGroupsUseCase = get(),
            addGroupUseCase = get(),
            deleteGroupUseCase = get(),
            deleteGroupPostsUseCase = get(),
        )
    }
}