package ru.nyxsed.postscan.features.imagepager.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.core.domain.models.Content
import ru.nyxsed.postscan.features.imagepager.domain.usecase.ChangeContentLikeStatusUseCase
import ru.nyxsed.postscan.features.imagepager.domain.usecase.CheckContentLikeStatusUseCase
import ru.nyxsed.postscan.features.imagepager.presentation.ImagePagerViewModel

val imagePagerModule = module {
    factory { CheckContentLikeStatusUseCase(get()) }
    factory { ChangeContentLikeStatusUseCase(get()) }

    viewModel {(contentList: List<Content>, pageIndex : Int) ->
        ImagePagerViewModel(
            getResourceUseCase = get(),
            getSettingBooleanFlowUseCase = get(),
            setSettingBooleanUseCase = get(),
            checkContentLikeStatusUseCase = get(),
            changeContentLikeStatusUseCase = get(),
            isInternetAvailableUseCase = get(),
            isTokenValidUseCase = get(),
            contentList = contentList,
            pageIndex = pageIndex,
        )
    }
}