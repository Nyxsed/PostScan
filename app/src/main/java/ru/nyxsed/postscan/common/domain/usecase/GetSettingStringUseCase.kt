package ru.nyxsed.postscan.common.domain.usecase

import ru.nyxsed.postscan.common.domain.models.SettingKey
import ru.nyxsed.postscan.common.domain.repository.DataStoreRepository

class GetSettingStringUseCase(private val repo: DataStoreRepository) {
    suspend operator fun invoke(key: SettingKey): String = repo.getString(key)
}