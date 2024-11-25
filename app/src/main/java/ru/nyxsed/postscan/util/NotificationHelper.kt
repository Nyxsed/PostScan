package ru.nyxsed.postscan.util

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.util.Constants.PROGRESS_CHANNEL_ID
import ru.nyxsed.postscan.util.Constants.PROGRESS_CHANNEL_NAME
import ru.nyxsed.postscan.util.Constants.PROGRESS_NOTIFICATION_ID

object NotificationHelper {
    fun initNotification(context: Context) {
        val notificationManager =
            GlobalContext.get().get<NotificationManager> { parametersOf(context, PROGRESS_CHANNEL_ID, PROGRESS_CHANNEL_NAME) }
        val builder: NotificationCompat.Builder = GlobalContext.get().get { parametersOf(context, PROGRESS_CHANNEL_ID) }

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
        builder.setProgress(100, progress, false)

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
            .setProgress(0, 0, false)

        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )
        notificationManager.notify(PROGRESS_NOTIFICATION_ID, builder.build())
    }
}