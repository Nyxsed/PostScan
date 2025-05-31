package ru.nyxsed.postscan.common.di

import androidx.room.Room
import org.koin.dsl.module
import ru.nyxsed.postscan.common.data.database.AppDatabase
import ru.nyxsed.postscan.common.data.mapper.VkMapper

//

val dbModule = module {
    // DB dependencies
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    factory {
        get<AppDatabase>().DbDao()
    }

    single {
        VkMapper()
    }
}