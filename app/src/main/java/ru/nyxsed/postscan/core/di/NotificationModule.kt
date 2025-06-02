package ru.nyxsed.postscan.core.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.nyxsed.postscan.core.data.util.NotificationHelperImpl
import ru.nyxsed.postscan.core.domain.util.NotificationHelper
import ru.nyxsed.postscan.core.notification.NotificationBuilderFactory
import ru.nyxsed.postscan.core.notification.NotificationChannelCreator

val notificationModule = module {
    single<NotificationChannelCreator> {
        NotificationChannelCreator(context = androidContext())
    }

    single<NotificationBuilderFactory> {
        NotificationBuilderFactory(context = androidContext())
    }

    single<NotificationHelper> {
        NotificationHelperImpl(
            channelCreator = get(),
            builderFactory = get(),
            context = androidContext(),
        )
    }
}