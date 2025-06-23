package ru.nyxsed.postscan.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.repository.DataStoreRepository

class GetSettingBooleanFlowUseCase(private val repo: DataStoreRepository) {
    operator fun invoke(key : SettingKey): Flow<Boolean> = repo.getBooleanFlow(key)
}