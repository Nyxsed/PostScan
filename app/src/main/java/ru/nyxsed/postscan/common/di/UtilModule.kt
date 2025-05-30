package ru.nyxsed.postscan.common.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import androidx.core.app.NotificationCompat
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.common.util.ConnectionChecker
import ru.nyxsed.postscan.features.preferences.data.util.AndroidCustomResourcesProvider
import ru.nyxsed.postscan.features.preferences.domain.util.CustomResourcesProvider

val utilModule = module {
    single<NotificationManager> { (context: Context, channelId: String, channelName: String) ->
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            enableVibration(false)
            setSound(null, null)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager
    }

    factory { (context: Context, channelId: String) ->
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_download)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentTitle(context.getString(R.string.loading_posts))
            .setContentText(context.getString(R.string.loading_progress))
            .setProgress(100, 0, false)
    }

    single<ConnectionChecker> {
        ConnectionChecker(
            context = get(),
        )
    }

    single<Resources> {
        androidContext().resources
    }

    single< CustomResourcesProvider> {
        AndroidCustomResourcesProvider(androidContext())
    }
}