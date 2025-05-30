package ru.nyxsed.postscan.di


import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.nyxsed.postscan.common.domain.models.entity.PostEntity
import ru.nyxsed.postscan.presentation.screens.changegroupscreen.ChangeGroupScreenViewModel
import ru.nyxsed.postscan.presentation.screens.commentsscreen.CommentsScreenViewModel
import ru.nyxsed.postscan.presentation.screens.groupsscreen.GroupsScreenViewModel
import ru.nyxsed.postscan.presentation.screens.imagepagerscreen.ImagePagerViewModel
import ru.nyxsed.postscan.presentation.screens.pickgroupscreen.PickGroupScreenViewModel
import ru.nyxsed.postscan.presentation.screens.postsscreen.PostsScreenViewModel

val appModule = module {
    // viewmodels
    viewModel<PostsScreenViewModel> {
        PostsScreenViewModel(
            dbRepository = get(),
            vkRepository = get(),
            dataStoreInteraction = get(),
            connectionChecker = get(),
            resources = get()
        )
    }

    viewModel<GroupsScreenViewModel> {
        GroupsScreenViewModel(
            dbRepository = get(),
            connectionChecker = get(),
            resources = get(),
            vkRepository = get(),
            dataStoreInteraction = get()
        )
    }

    viewModel<PickGroupScreenViewModel> {
        PickGroupScreenViewModel(
            dbRepository = get(),
            vkRepository = get(),
            connectionChecker = get(),
            resources = get(),
        )
    }

    viewModel<ChangeGroupScreenViewModel> {
        ChangeGroupScreenViewModel(
            vkRepository = get(),
            dbRepository = get(),
            connectionChecker = get(),
            resources = get()
        )
    }

    viewModel<ImagePagerViewModel> {
        ImagePagerViewModel(
            vkRepository = get(),
            connectionChecker = get(),
            resources = get(),
            dataStoreInteraction = get(),
        )
    }

    viewModel<CommentsScreenViewModel> { (post: PostEntity) ->
        CommentsScreenViewModel(
            vkRepository = get(),
            post = post,
            dataStoreInteraction = get()
        )
    }
}