package ru.nyxsed.postscan.core.di

import org.koin.dsl.module
import ru.nyxsed.postscan.core.domain.usecase.AddPostUseCase
import ru.nyxsed.postscan.core.domain.usecase.DeleteGroupPostsUseCase
import ru.nyxsed.postscan.core.domain.usecase.DeleteGroupUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetAllGroupsUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetPostsForGroupDateIntervalUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetResourceUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetSettingBooleanFlowUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetSettingStringUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.core.domain.usecase.SetSettingBooleanUseCase
import ru.nyxsed.postscan.core.domain.usecase.SetSettingStringUseCase
import ru.nyxsed.postscan.core.domain.usecase.UpdateGroupUseCase

val coreModule = module {
    factory { GetSettingBooleanFlowUseCase(get()) }
    factory { SetSettingBooleanUseCase(get()) }
    factory { GetSettingStringUseCase(get()) }
    factory { SetSettingStringUseCase(get()) }
    factory { IsInternetAvailableUseCase(get()) }
    factory { IsTokenValidUseCase(get()) }
    factory { GetAllGroupsUseCase(get()) }
    factory { DeleteGroupUseCase(get()) }
    factory { DeleteGroupPostsUseCase(get()) }
    factory { AddPostUseCase(get()) }
    factory { GetPostsForGroupDateIntervalUseCase(get(), get(), get()) }
    factory { UpdateGroupUseCase(get()) }
    factory { GetResourceUseCase(get()) }
}