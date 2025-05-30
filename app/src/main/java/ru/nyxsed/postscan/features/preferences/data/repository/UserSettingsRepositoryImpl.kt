package ru.nyxsed.postscan.features.preferences.data.repository

import ru.nyxsed.postscan.common.util.DataStoreInteraction
import ru.nyxsed.postscan.features.preferences.domain.model.SettingKey
import ru.nyxsed.postscan.features.preferences.domain.repository.UserSettingsRepository

class UserSettingsRepositoryImpl(
    private val dataStoreInteraction: DataStoreInteraction
) : UserSettingsRepository {
    override suspend fun getBoolean(key: SettingKey): Boolean =
        dataStoreInteraction.getSettingBooleanFromDataStore(key.name)

    override suspend fun setBoolean(key: SettingKey, value: Boolean) =
        dataStoreInteraction.saveSettingBooleanToDataStore(key.name, value)
}