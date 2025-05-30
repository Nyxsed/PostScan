package ru.nyxsed.postscan.features.preferences.domain.usecase

import ru.nyxsed.postscan.features.preferences.domain.model.SettingKey
import ru.nyxsed.postscan.features.preferences.domain.repository.UserSettingsRepository

class SetSettingUseCase(private val repo: UserSettingsRepository) {
    suspend operator fun invoke(key: SettingKey, value: Boolean) = repo.setBoolean(key, value)
}
