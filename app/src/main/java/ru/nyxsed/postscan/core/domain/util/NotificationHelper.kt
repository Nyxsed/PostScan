package ru.nyxsed.postscan.core.domain.util

interface NotificationHelper {
    fun initNotification()
    fun updateProgressNotification(percent: Int)
    fun completeNotification()
    fun errorNotification(message: String)
}