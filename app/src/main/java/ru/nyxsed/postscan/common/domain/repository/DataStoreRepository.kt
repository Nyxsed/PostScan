package ru.nyxsed.postscan.common.domain.repository

import ru.nyxsed.postscan.common.domain.models.SettingKey

interface DataStoreRepository {
    suspend fun getBoolean(key: SettingKey): Boolean
    suspend fun setBoolean(key: SettingKey, value: Boolean)
}