package ru.nyxsed.postscan.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class DataStoreInteraction(
    private val dataStore: DataStore<Preferences>,
) {

    suspend fun saveSettingToDataStore(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    suspend fun getSettingFromDataStore(key: String): String {
        val preferences = dataStore.data.first()
        return preferences[stringPreferencesKey(key)] ?: "default_value"
    }

    // boolean
    suspend fun saveSettingBooleanToDataStore(key: String, value: Boolean) {
        val stringValue = if (value) "1" else "0"
        saveSettingToDataStore(key, stringValue)
    }

    suspend fun getSettingBooleanFromDataStore(key: String): Boolean {
        val setting = getSettingFromDataStore(key)
        return setting == "1"
    }

    companion object {
        const val NOT_LOAD_LIKED_POSTS = "NOT_LOAD_LIKED_POSTS"
        const val USE_MIHON = "USE_MIHON"
        const val DELETE_AFTER_LIKE = "DELETE_AFTER_LIKE"
        const val NOTIFICATION_PERMISSION_REQUESTED = "NOTIFICATION_PERMISSION_REQUESTED"
        const val SHOWED_TUTORIAL_POSTS = "SHOWED_TUTORIAL_POSTS"
        const val SHOWED_TUTORIAL_GROUPS = "SHOWED_TUTORIAL_GROUPS"
        const val SHOWED_TUTORIAL_IMAGE = "SHOWED_TUTORIAL_IMAGE"
    }
}