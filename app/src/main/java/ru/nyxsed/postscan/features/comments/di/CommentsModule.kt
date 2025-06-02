package ru.nyxsed.postscan.features.comments.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.core.domain.models.entity.Post
import ru.nyxsed.postscan.features.comments.domain.usecase.GetCommentsUseCase
import ru.nyxsed.postscan.features.comments.presentation.CommentsScreenViewModel

val commentsModule = module {
    factory { GetCommentsUseCase(get()) }

    viewModel { (post: Post) ->
        CommentsScreenViewModel(
            post = post,
            getCommentsUseCase = get(),
            getSettingBooleanUseCase = get(),
        )
    }
}