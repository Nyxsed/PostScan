package ru.nyxsed.postscan.common.data.repository

import ru.nyxsed.postscan.common.domain.models.SettingKey
import ru.nyxsed.postscan.common.domain.repository.DataStoreRepository
import ru.nyxsed.postscan.common.util.DataStoreInteraction

class DataStoreRepositoryImpl(
    private val dataStoreInteraction: DataStoreInteraction
) : DataStoreRepository {
    override suspend fun getBoolean(key: SettingKey): Boolean =
        dataStoreInteraction.getSettingBooleanFromDataStore(key.name)

    override suspend fun setBoolean(key: SettingKey, value: Boolean) =
        dataStoreInteraction.saveSettingBooleanToDataStore(key.name, value)
}