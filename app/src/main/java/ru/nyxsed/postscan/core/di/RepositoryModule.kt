package ru.nyxsed.postscan.core.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.nyxsed.postscan.core.data.repository.DataStoreRepositoryImpl
import ru.nyxsed.postscan.core.data.repository.DbRepositoryImpl
import ru.nyxsed.postscan.core.data.repository.VkRepositoryImpl
import ru.nyxsed.postscan.core.data.util.ConnectionCheckerImpl
import ru.nyxsed.postscan.core.data.util.CustomResourcesProviderImpl
import ru.nyxsed.postscan.core.domain.repository.DataStoreRepository
import ru.nyxsed.postscan.core.domain.repository.DbRepository
import ru.nyxsed.postscan.core.domain.repository.VkRepository
import ru.nyxsed.postscan.core.domain.util.ConnectionChecker
import ru.nyxsed.postscan.core.domain.util.CustomResourcesProvider

val repositoryModule = module {
    single<DataStoreRepository> {
        DataStoreRepositoryImpl(dataStore = get())
    }

    single<VkRepository> {
        VkRepositoryImpl(
            apiService = get(),
            mapper = get(),
            dataStoreRepository = get()
        )
    }

    single<DbRepository> {
        DbRepositoryImpl(
            dbDao = get(),
            context = androidContext()
        )
    }

    single<CustomResourcesProvider> {
        CustomResourcesProviderImpl(androidContext())
    }

    single<ConnectionChecker> {
        ConnectionCheckerImpl(
            context = get(),
        )
    }
}