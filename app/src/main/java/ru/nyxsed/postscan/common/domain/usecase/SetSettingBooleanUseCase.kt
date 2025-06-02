package ru.nyxsed.postscan.common.domain.usecase

import ru.nyxsed.postscan.common.domain.models.SettingKey
import ru.nyxsed.postscan.common.domain.repository.DataStoreRepository

class SetSettingBooleanUseCase(private val repo: DataStoreRepository) {
    suspend operator fun invoke(key: SettingKey, value: Boolean) = repo.setBoolean(key, value)
}
