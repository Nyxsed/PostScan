package ru.nyxsed.postscan

import android.app.Application
import com.vk.id.VKID
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.nyxsed.postscan.di.appModule
import ru.nyxsed.postscan.di.commonModule
import ru.nyxsed.postscan.di.dbModule
import ru.nyxsed.postscan.di.loginModule
import ru.nyxsed.postscan.di.networkModule
import ru.nyxsed.postscan.di.preferencesModule
import ru.nyxsed.postscan.di.utilModule
import java.util.Locale

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        VKID.init(this)
        VKID.instance.setLocale(Locale("ru"))

        startKoin {
            androidContext(this@App)
            modules(commonModule,appModule, dbModule, networkModule, utilModule, preferencesModule, loginModule)
        }
    }
}