package ru.nyxsed.postscan.features.posts.di


import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.features.posts.domain.usecase.ChangePostLikeStatusUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.DeletePostUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.GetAllPostsUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.GetPostsForGroupUseCase
import ru.nyxsed.postscan.features.posts.domain.usecase.UpdatePostUseCase
import ru.nyxsed.postscan.features.posts.presentation.PostsScreenViewModel

val postsModule = module {
    factory { GetAllPostsUseCase(get()) }
    factory { DeletePostUseCase(get()) }
    factory { UpdatePostUseCase(get()) }
    factory { GetPostsForGroupUseCase(get(), get(), get()) }
    factory { ChangePostLikeStatusUseCase(get()) }

    viewModel {
        PostsScreenViewModel(
            getResourceUseCase = get(),
            isInternetAvailableUseCase = get(),
            isTokenValidUseCase = get(),
            getSettingStringUseCase = get(),
            setSettingStringUseCase = get(),
            getSettingBooleanUseCase = get(),
            setSettingBooleanUseCase = get(),
            getAllPostsUseCase = get(),
            getAllGroupsUseCase = get(),
            addPostUseCase = get(),
            updateGroupUseCase = get(),
            deletePostUseCase = get(),
            updatePostUseCase = get(),
            getPostsForGroupUseCase = get(),
            changePostLikeStatusUseCase = get(),
            notificationHelper = get(),
        )
    }
}