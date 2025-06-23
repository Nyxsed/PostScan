package ru.nyxsed.postscan.core.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import ru.nyxsed.postscan.R

/**
 * Фабрика для создания уведомлений с базовой настройкой для загрузки постов.
 */
class NotificationBuilderFactory(private val context: Context) {

    fun create(channelId: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_download)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentTitle(context.getString(R.string.loading_posts))
            .setContentText(context.getString(R.string.loading_progress))
            .setProgress(100, 0, false)
    }
}