package ru.nyxsed.postscan.core.domain.models

data class Group(
    val groupId: Long,
    val name: String,
    val screenName: String,
    val avatarUrl: String,
    var lastFetchDate: Long,
)
