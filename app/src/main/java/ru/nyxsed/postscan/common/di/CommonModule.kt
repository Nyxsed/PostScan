package ru.nyxsed.postscan.common.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.nyxsed.postscan.common.data.repository.DataStoreRepositoryImpl
import ru.nyxsed.postscan.common.data.repository.DbRepositoryImpl
import ru.nyxsed.postscan.common.data.repository.VkRepositoryImpl
import ru.nyxsed.postscan.common.domain.repository.DataStoreRepository
import ru.nyxsed.postscan.common.domain.repository.DbRepository
import ru.nyxsed.postscan.common.domain.repository.VkRepository
import ru.nyxsed.postscan.common.domain.usecase.GetSettingUseCase
import ru.nyxsed.postscan.common.domain.usecase.SetSettingUseCase

val commonModule = module {
    single<DataStoreRepository> { DataStoreRepositoryImpl(get()) }

    single<VkRepository> {
        VkRepositoryImpl(
            apiService = get(),
            mapper = get(),
            dataStoreInteraction = get()
        )
    }

    single<DbRepository> {
        DbRepositoryImpl(
            dbDao = get(),
            context = androidContext()
        )
    }

    factory { GetSettingUseCase(get()) }
    factory { SetSettingUseCase(get()) }
}