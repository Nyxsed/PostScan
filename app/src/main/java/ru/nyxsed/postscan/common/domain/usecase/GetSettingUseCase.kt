package ru.nyxsed.postscan.common.domain.usecase

import ru.nyxsed.postscan.common.domain.models.SettingKey
import ru.nyxsed.postscan.common.domain.repository.DataStoreRepository

class GetSettingUseCase(private val repo: DataStoreRepository) {
    suspend operator fun invoke(key: SettingKey): Boolean = repo.getBoolean(key)
}