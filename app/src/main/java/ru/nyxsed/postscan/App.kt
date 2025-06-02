package ru.nyxsed.postscan

import android.app.Application
import com.vk.id.VKID
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.nyxsed.postscan.core.di.coreModule
import ru.nyxsed.postscan.core.di.dataStoreModule
import ru.nyxsed.postscan.core.di.databaseModule
import ru.nyxsed.postscan.core.di.networkModule
import ru.nyxsed.postscan.core.di.notificationModule
import ru.nyxsed.postscan.core.di.repositoryModule
import ru.nyxsed.postscan.features.changegroup.di.changeGroupModule
import ru.nyxsed.postscan.features.comments.di.commentsModule
import ru.nyxsed.postscan.features.groups.di.groupsModule
import ru.nyxsed.postscan.features.imagepager.di.imagePagerModule
import ru.nyxsed.postscan.features.login.di.loginModule
import ru.nyxsed.postscan.features.pickgroup.di.pickGroupModule
import ru.nyxsed.postscan.features.posts.di.postsModule
import ru.nyxsed.postscan.features.preferences.di.preferencesModule
import java.util.Locale

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        VKID.init(this)
        VKID.instance.setLocale(Locale("ru"))

        startKoin {
            androidContext(this@App)
            modules(
                databaseModule,
                networkModule,
                notificationModule,
                dataStoreModule,
                repositoryModule,
                coreModule,
                postsModule,
                preferencesModule,
                loginModule,
                commentsModule,
                imagePagerModule,
                pickGroupModule,
                groupsModule,
                changeGroupModule
            )
        }
    }
}