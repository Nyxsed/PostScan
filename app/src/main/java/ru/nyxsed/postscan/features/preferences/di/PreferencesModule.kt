package ru.nyxsed.postscan.features.preferences.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.features.preferences.domain.usecase.ExportDbUseCase
import ru.nyxsed.postscan.features.preferences.domain.usecase.ImportDbUseCase
import ru.nyxsed.postscan.features.preferences.presentation.PreferencesScreenViewModel

val preferencesModule = module {
    factory { ExportDbUseCase(get()) }
    factory { ImportDbUseCase(get()) }

    viewModel {
        PreferencesScreenViewModel(
            getSettingBooleanUseCase = get(),
            setSettingBooleanUseCase = get(),
            exportDbUseCase = get(),
            importDbUseCase = get(),
            getResourceUseCase = get(),
        )
    }
}