package ru.nyxsed.postscan.features.imagepager.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.features.imagepager.domain.usecase.ChangeLikeStatusUseCase
import ru.nyxsed.postscan.features.imagepager.domain.usecase.CheckLikeStatusUseCase
import ru.nyxsed.postscan.features.imagepager.presentation.ImagePagerViewModel

val imagepagerModule = module {
    factory { CheckLikeStatusUseCase(get()) }
    factory { ChangeLikeStatusUseCase(get()) }

    viewModel {
        ImagePagerViewModel(
            customResourceProvider = get(),
            getSettingUseCase = get(),
            setSettingUseCase = get(),
            checkLikeStatusUseCase = get(),
            changeLikeStatusUseCase = get(),
            isInternetAvailableUseCase = get(),
            isTokenValidUseCase = get()
        )
    }
}