package ru.nyxsed.postscan.features.changegroup.presentation

data class ChangeGroupState (
    val groupId: Long = 0L,
    val groupName : String = "",
    val screenName: String = "",
    val avatarUrl: String = "",
    val lastFetchDate: String = "",
    val showDeleteDialog: Boolean = false,
    val showDownloadDialog: Boolean = false,
    val showCircularIndicator: Boolean = false,
)