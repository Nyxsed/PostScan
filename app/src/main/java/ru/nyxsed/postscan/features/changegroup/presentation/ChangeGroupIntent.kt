package ru.nyxsed.postscan.features.changegroup.presentation

sealed class ChangeGroupIntent {
    object ToggleDeleteDialog : ChangeGroupIntent()
    object ToggleDownloadDialog : ChangeGroupIntent()
    data class ChangeGroupName(val value: String) : ChangeGroupIntent()
    data class ChangeLastFetchDate(val value: String) : ChangeGroupIntent()
    object UpdateGroup : ChangeGroupIntent()
    object OpenGroupUri : ChangeGroupIntent()
    object DeleteGroupPosts : ChangeGroupIntent()
    data class LoadPosts(val startDate: String, val endDate: String) : ChangeGroupIntent()
}