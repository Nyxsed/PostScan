package ru.nyxsed.postscan

import android.app.Application
import com.vk.id.VKID
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.nyxsed.postscan.common.di.appModule
import ru.nyxsed.postscan.common.di.commonModule
import ru.nyxsed.postscan.common.di.dbModule
import ru.nyxsed.postscan.common.di.networkModule
import ru.nyxsed.postscan.common.di.utilModule
import ru.nyxsed.postscan.features.comments.di.commentsModule
import ru.nyxsed.postscan.features.imagepager.di.imagepagerModule
import ru.nyxsed.postscan.features.login.di.loginModule
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
                commonModule,
                appModule,
                dbModule,
                networkModule,
                utilModule,
                preferencesModule,
                loginModule,
                commentsModule,
                imagepagerModule
            )
        }
    }
}