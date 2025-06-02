package ru.nyxsed.postscan.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.nyxsed.postscan.core.data.models.entity.GroupEntity
import ru.nyxsed.postscan.core.data.models.entity.PostEntity

@Database(entities = [PostEntity::class, GroupEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun DbDao(): DbDao
}