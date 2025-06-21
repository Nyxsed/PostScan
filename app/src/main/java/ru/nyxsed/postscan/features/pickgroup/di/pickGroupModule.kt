package ru.nyxsed.postscan.features.pickgroup.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.core.domain.models.PickGroupMode
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.AddGroupUseCase
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.GetUserGroupsUseCase
import ru.nyxsed.postscan.features.pickgroup.domain.usecase.SearchGroupsUseCase
import ru.nyxsed.postscan.features.pickgroup.presentation.PickGroupViewModel

val pickGroupModule = module {
    factory { GetUserGroupsUseCase(get()) }
    factory { SearchGroupsUseCase(get(), get()) }
    factory { AddGroupUseCase(get()) }

    viewModel {(mode : PickGroupMode) ->
        PickGroupViewModel(
            getResourceUseCase = get(),
            isInternetAvailableUseCase = get(),
            isTokenValidUseCase = get(),
            getUserGroupsUseCase = get(),
            searchGroupsUseCase = get(),
            getAllGroupsUseCase = get(),
            addGroupUseCase = get(),
            deleteGroupUseCase = get(),
            deleteGroupPostsUseCase = get(),
            mode = mode,
        )
    }
}