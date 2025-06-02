package ru.nyxsed.postscan.core.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.nyxsed.postscan.core.domain.models.entity.ContentEntity

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromListContentEntity(list: List<ContentEntity>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toListContentEntity(data: String?): List<ContentEntity> {
        val listType = object : TypeToken<List<ContentEntity>>() {}.type
        return gson.fromJson(data, listType)
    }
}