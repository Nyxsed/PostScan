package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.repository.DataStoreRepository

class GetSettingStringUseCase(private val repo: DataStoreRepository) {
    suspend operator fun invoke(key: SettingKey): String = repo.getString(key)
}