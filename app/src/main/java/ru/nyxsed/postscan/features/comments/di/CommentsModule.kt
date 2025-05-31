package ru.nyxsed.postscan.features.comments.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.common.domain.models.entity.PostEntity
import ru.nyxsed.postscan.features.comments.domain.usecase.GetCommentsUseCase
import ru.nyxsed.postscan.features.comments.presentation.screens.commentsscreen.CommentsScreenViewModel

val commentsModule = module {
    factory { GetCommentsUseCase(get()) }

    viewModel { (post: PostEntity) ->
        CommentsScreenViewModel(
            post = post,
            getCommentsUseCase = get(),
            getSettingUseCase = get(),
        )
    }
}