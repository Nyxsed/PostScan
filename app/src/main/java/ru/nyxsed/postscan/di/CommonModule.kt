package ru.nyxsed.postscan.di

import org.koin.dsl.module
import ru.nyxsed.postscan.common.data.repository.VkRepositoryImpl
import ru.nyxsed.postscan.common.domain.repository.VkRepository

val commonModule = module {
    single<VkRepository> {
        VkRepositoryImpl(
            apiService = get(),
            mapper = get(),
            dataStoreInteraction = get()
        )
    }
}