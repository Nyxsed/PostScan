package ru.nyxsed.postscan.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.nyxsed.postscan.common.data.repository.DataStoreRepositoryImpl
import ru.nyxsed.postscan.common.data.repository.DbRepositoryImpl
import ru.nyxsed.postscan.common.data.repository.VkRepositoryImpl
import ru.nyxsed.postscan.common.data.util.ConnectionCheckerImpl
import ru.nyxsed.postscan.common.data.util.CustomResourcesProviderImpl
import ru.nyxsed.postscan.common.domain.repository.DataStoreRepository
import ru.nyxsed.postscan.common.domain.repository.DbRepository
import ru.nyxsed.postscan.common.domain.repository.VkRepository
import ru.nyxsed.postscan.common.domain.usecase.AddPostUseCase
import ru.nyxsed.postscan.common.domain.usecase.DeleteGroupPostsUseCase
import ru.nyxsed.postscan.common.domain.usecase.DeleteGroupUseCase
import ru.nyxsed.postscan.common.domain.usecase.GetAllGroupsUseCase
import ru.nyxsed.postscan.common.domain.usecase.GetPostsForGroupDateIntervalUseCase
import ru.nyxsed.postscan.common.domain.usecase.GetSettingBooleanUseCase
import ru.nyxsed.postscan.common.domain.usecase.GetSettingStringUseCase
import ru.nyxsed.postscan.common.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.common.domain.usecase.IsTokenValidUseCase
import ru.nyxsed.postscan.common.domain.usecase.SetSettingBooleanUseCase
import ru.nyxsed.postscan.common.domain.usecase.SetSettingStringUseCase
import ru.nyxsed.postscan.common.domain.usecase.UpdateGroupUseCase
import ru.nyxsed.postscan.common.domain.util.ConnectionChecker
import ru.nyxsed.postscan.common.domain.util.CustomResourcesProvider

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val commonModule = module {
    single<DataStore<Preferences>> { get<Context>().dataStore }
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

    factory { GetSettingBooleanUseCase(get()) }
    factory { SetSettingBooleanUseCase(get()) }
    factory { GetSettingStringUseCase(get()) }
    factory { SetSettingStringUseCase(get()) }
    factory { IsInternetAvailableUseCase(get()) }
    factory { IsTokenValidUseCase(get()) }
    factory { GetAllGroupsUseCase(get()) }
    factory { DeleteGroupUseCase(get()) }
    factory { DeleteGroupPostsUseCase(get()) }
    factory { AddPostUseCase(get()) }
    factory { GetPostsForGroupDateIntervalUseCase(get()) }
    factory { UpdateGroupUseCase(get()) }
}