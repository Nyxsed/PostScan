package ru.nyxsed.postscan.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.repository.DataStoreRepository

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : DataStoreRepository {

    override suspend fun getBoolean(key: SettingKey): Boolean {
        return try {
            val prefs = dataStore.data.first()
            prefs[booleanPreferencesKey(key.toString())] ?: false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getString(key: SettingKey): String {
        return try {
            val preferences = dataStore.data.first()
            preferences[stringPreferencesKey(key.toString())] ?: "default_value"
        } catch (e: Exception) {
            "default_value"
        }
    }

    override suspend fun setBoolean(key: SettingKey, value: Boolean) {
        try {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(key.toString())] = value
            }
        } catch (e: Exception) { }
    }

    override suspend fun setString(key: SettingKey, value: String) {
        try {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey(key.toString())] = value
            }
        } catch (e: Exception) { }
    }

    override fun getBooleanFlow(key: SettingKey): Flow<Boolean> =
        dataStore.data
            .catch { e ->
                false
            }
            .map { prefs ->
                prefs[booleanPreferencesKey(key.toString())] ?: false
            }
}