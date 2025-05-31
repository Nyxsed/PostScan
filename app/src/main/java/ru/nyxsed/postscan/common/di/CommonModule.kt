package ru.nyxsed.postscan.common.di

import org.koin.dsl.module
import ru.nyxsed.postscan.common.data.repository.DbRepositoryImpl
import ru.nyxsed.postscan.common.data.repository.VkRepositoryImpl
import ru.nyxsed.postscan.common.domain.repository.DbRepository
import ru.nyxsed.postscan.common.domain.repository.VkRepository
import ru.nyxsed.postscan.features.preferences.data.repository.UserSettingsRepositoryImpl
import ru.nyxsed.postscan.features.preferences.domain.repository.UserSettingsRepository
import ru.nyxsed.postscan.features.preferences.domain.usecase.GetSettingUseCase

val commonModule = module {
    single<UserSettingsRepository> { UserSettingsRepositoryImpl(get()) }

    single<VkRepository> {
        VkRepositoryImpl(
            apiService = get(),
            mapper = get(),
            dataStoreInteraction = get()
        )
    }
    
    single<DbRepository> {
        DbRepositoryImpl(
            dbDao = get()
        )
    }

    factory { GetSettingUseCase(get()) }
}