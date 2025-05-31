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
import ru.nyxsed.postscan.common.domain.repository.DataStoreRepository
import ru.nyxsed.postscan.common.domain.repository.DbRepository
import ru.nyxsed.postscan.common.domain.repository.VkRepository
import ru.nyxsed.postscan.common.domain.usecase.GetSettingUseCase
import ru.nyxsed.postscan.common.domain.usecase.SetSettingUseCase

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

    factory { GetSettingUseCase(get()) }
    factory { SetSettingUseCase(get()) }
}