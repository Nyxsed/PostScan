package ru.nyxsed.postscan.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.nyxsed.postscan.core.domain.models.SettingKey

interface DataStoreRepository {
    suspend fun getBoolean(key: SettingKey): Boolean
    fun getBooleanFlow(key: SettingKey): Flow<Boolean>
    suspend fun setBoolean(key: SettingKey, value: Boolean)
    suspend fun getString(key: SettingKey): String
    suspend fun setString(key: SettingKey, value: String)
}