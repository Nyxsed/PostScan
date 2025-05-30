package ru.nyxsed.postscan.features.preferences.domain.repository

import ru.nyxsed.postscan.features.preferences.domain.model.SettingKey

interface UserSettingsRepository {
    suspend fun getBoolean(key: SettingKey): Boolean
    suspend fun setBoolean(key: SettingKey, value: Boolean)
}