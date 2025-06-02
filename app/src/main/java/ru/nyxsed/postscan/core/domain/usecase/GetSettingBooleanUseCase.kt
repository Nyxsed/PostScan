package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.repository.DataStoreRepository

class GetSettingBooleanUseCase(private val repo: DataStoreRepository) {
    suspend operator fun invoke(key: SettingKey): Boolean = repo.getBoolean(key)
}