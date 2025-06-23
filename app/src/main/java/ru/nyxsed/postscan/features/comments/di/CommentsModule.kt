package ru.nyxsed.postscan.features.comments.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.features.comments.domain.usecase.GetCommentsUseCase
import ru.nyxsed.postscan.features.comments.presentation.CommentsViewModel

val commentsModule = module {
    factory { GetCommentsUseCase(get()) }

    viewModel { (post: Post) ->
        CommentsViewModel(
            post = post,
            getCommentsUseCase = get(),
            getSettingBooleanFlowUseCase = get(),
        )
    }
}