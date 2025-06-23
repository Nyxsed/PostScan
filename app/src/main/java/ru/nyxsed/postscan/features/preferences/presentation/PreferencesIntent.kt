package ru.nyxsed.postscan.features.preferences.presentation

import android.net.Uri
import ru.nyxsed.postscan.core.domain.models.SettingKey

sealed class PreferencesIntent {
    data class ToggleSetting(val key: SettingKey, val value: Boolean) : PreferencesIntent()
    data class ImportDB(val uri: Uri) : PreferencesIntent()
    data class ExportDB(val uri: Uri) : PreferencesIntent()
    object Logout : PreferencesIntent()
    object ResetTutorial : PreferencesIntent()
}