package ru.nyxsed.postscan.core.data.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.util.NotificationHelper
import ru.nyxsed.postscan.core.notification.NotificationBuilderFactory
import ru.nyxsed.postscan.core.notification.NotificationChannelCreator

class NotificationHelperImpl(
    private val channelCreator: NotificationChannelCreator,
    private val builderFactory: NotificationBuilderFactory,
    private val context: Context,
) : NotificationHelper {

    private val channelId = "progress_channel_id"
    private val channelName = "Progress Channel"
    private val notificationId = 1

    private val notificationManager by lazy {
        channelCreator.createChannel(channelId, channelName)
    }

    private val builder by lazy {
        builderFactory.create(channelId)
    }

    override fun initNotification() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(null)
            notificationManager.notify(notificationId, builder.build())
        }
    }

    override fun updateProgressNotification(progress: Int) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            builder.setProgress(100, progress, false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
            notificationManager.notify(notificationId, builder.build())
        }
    }

    override fun completeNotification() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            builder.setContentText(context.getString(R.string.loading_completed))
                .setSmallIcon(R.drawable.ic_checkmark)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setProgress(0, 0, false)
            notificationManager.notify(notificationId, builder.build())
        }
    }

    override fun errorNotification(message: String) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            builder.setContentText(message)
                .setSmallIcon(R.drawable.ic_close)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setProgress(0, 0, false)
                .setVibrate(longArrayOf(100))
            notificationManager.notify(notificationId, builder.build())
        }
    }
}