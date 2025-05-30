package ru.nyxsed.postscan.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.features.preferences.data.repository.DatabaseRepositoryImpl
import ru.nyxsed.postscan.features.preferences.data.repository.UserSettingsRepositoryImpl
import ru.nyxsed.postscan.features.preferences.domain.repository.DatabaseRepository
import ru.nyxsed.postscan.features.preferences.domain.repository.UserSettingsRepository
import ru.nyxsed.postscan.features.preferences.domain.usecase.ExportDbUseCase
import ru.nyxsed.postscan.features.preferences.domain.usecase.GetSettingUseCase
import ru.nyxsed.postscan.features.preferences.domain.usecase.ImportDbUseCase
import ru.nyxsed.postscan.features.preferences.domain.usecase.SetSettingUseCase
import ru.nyxsed.postscan.features.preferences.presentation.screens.preferencesscreen.PreferencesScreenViewModel

val preferencesModule = module {
    single<UserSettingsRepository> { UserSettingsRepositoryImpl(get()) }
    single<DatabaseRepository> { DatabaseRepositoryImpl(get(), androidContext()) }

    factory { GetSettingUseCase(get()) }
    factory { SetSettingUseCase(get()) }
    factory { ExportDbUseCase(get()) }
    factory { ImportDbUseCase(get()) }

    viewModel {
        PreferencesScreenViewModel(
            getSetting = get(),
            setSetting = get(),
            exportDb = get(),
            importDb = get(),
            resourcesProvider = get(),
        )
    }
}