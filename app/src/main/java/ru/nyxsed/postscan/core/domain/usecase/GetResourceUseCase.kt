package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.util.CustomResourcesProvider

class GetResourceUseCase(private val customResourcesProvider: CustomResourcesProvider) {
    suspend operator fun invoke(resId: Int) = customResourcesProvider.getString(resId)
}