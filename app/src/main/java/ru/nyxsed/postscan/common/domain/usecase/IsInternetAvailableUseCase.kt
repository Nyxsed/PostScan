package ru.nyxsed.postscan.common.domain.usecase

import ru.nyxsed.postscan.common.domain.util.ConnectionChecker

class IsInternetAvailableUseCase(private val connectionChecker: ConnectionChecker) {
    operator fun invoke() = connectionChecker.isInternetAvailable()
}