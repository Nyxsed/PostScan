package ru.nyxsed.postscan.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.repository.DataStoreRepository

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : DataStoreRepository {
    override suspend fun setBoolean(key: SettingKey, value: Boolean) {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(key.toString())] = value
        }
    }

    override suspend fun getBoolean(key: SettingKey): Boolean {
        val prefs = dataStore.data.first()
        return prefs[booleanPreferencesKey(key.toString())] ?: false
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

    override fun getBooleanFlow(key: SettingKey): Flow<Boolean> =
        dataStore.data
            .map { prefs ->
                prefs[booleanPreferencesKey(key.toString())] ?: false
            }
}