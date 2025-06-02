package ru.nyxsed.postscan.core.di

import androidx.room.Room
import org.koin.dsl.module
import ru.nyxsed.postscan.core.data.database.AppDatabase
import ru.nyxsed.postscan.core.data.mapper.VkMapper

//

val databaseModule = module {
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