package ru.nyxsed.postscan.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.repository.DataStoreRepository

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : DataStoreRepository {
    override suspend fun setBoolean(key: SettingKey, value: Boolean) {
        val stringValue = if (value) "1" else "0"
        setString(key, stringValue)
    }

    override suspend fun getBoolean(key: SettingKey): Boolean {
        val setting = getString(key)
        return setting == "1"
    }

    override suspend fun getString(key: SettingKey): String {
        val preferences = dataStore.data.first()
        return preferences[stringPreferencesKey(key.toString())] ?: "default_value"
    }

    override suspend fun setString(key: SettingKey, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key.toString())] = value
        }
    }
}