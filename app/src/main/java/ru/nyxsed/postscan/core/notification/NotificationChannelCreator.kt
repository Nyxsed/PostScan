package ru.nyxsed.postscan.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class NotificationChannelCreator(private val context: Context) {

    fun createChannel(channelId: String, channelName: String): NotificationManager {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            enableVibration(false)
            setSound(null, null)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        return notificationManager
    }
}