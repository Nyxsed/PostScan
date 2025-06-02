package ru.nyxsed.postscan.core.util

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.util.Constants.PROGRESS_CHANNEL_ID
import ru.nyxsed.postscan.core.util.Constants.PROGRESS_CHANNEL_NAME
import ru.nyxsed.postscan.core.util.Constants.PROGRESS_NOTIFICATION_ID

object NotificationHelper {
    fun initNotification(context: Context) {
        val notificationManager =
            GlobalContext.get().get<NotificationManager> { parametersOf(context, PROGRESS_CHANNEL_ID, PROGRESS_CHANNEL_NAME) }
        val builder: NotificationCompat.Builder = GlobalContext.get().get { parametersOf(context, PROGRESS_CHANNEL_ID) }
        builder
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)

        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )
        notificationManager.notify(PROGRESS_NOTIFICATION_ID, builder.build())
    }

    fun updateProgress(context: Context, progress: Int) {
        val notificationManager =
            GlobalContext.get().get<NotificationManager> { parametersOf(context, PROGRESS_CHANNEL_ID, PROGRESS_CHANNEL_NAME) }
        val builder: NotificationCompat.Builder = GlobalContext.get().get { parametersOf(context, PROGRESS_CHANNEL_ID) }
        builder
            .setProgress(100, progress, false)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )
        notificationManager.notify(PROGRESS_NOTIFICATION_ID, builder.build())
    }

    fun completeNotification(context: Context) {
        val notificationManager =
            GlobalContext.get().get<NotificationManager> { parametersOf(context, PROGRESS_CHANNEL_ID, PROGRESS_CHANNEL_NAME) }
        val builder: NotificationCompat.Builder = GlobalContext.get().get { parametersOf(context, PROGRESS_CHANNEL_ID) }

        builder
            .setContentText(context.getString(R.string.loading_completed))
            .setSmallIcon(R.drawable.ic_checkmark)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(0, 0, false)

        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )
        notificationManager.notify(PROGRESS_NOTIFICATION_ID, builder.build())
    }

    fun errorNotification(context: Context, message: String) {
        val notificationManager =
            GlobalContext.get().get<NotificationManager> { parametersOf(context, PROGRESS_CHANNEL_ID, PROGRESS_CHANNEL_NAME) }
        val builder: NotificationCompat.Builder = GlobalContext.get().get { parametersOf(context, PROGRESS_CHANNEL_ID) }

        builder
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_close)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(0, 0, false)
            .setVibrate(longArrayOf(100))

        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )
        notificationManager.notify(PROGRESS_NOTIFICATION_ID, builder.build())
    }
}