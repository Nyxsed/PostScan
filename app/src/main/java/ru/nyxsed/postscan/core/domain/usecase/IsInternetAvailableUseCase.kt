package ru.nyxsed.postscan.core.domain.usecase

import ru.nyxsed.postscan.core.domain.util.ConnectionChecker

class IsInternetAvailableUseCase(private val connectionChecker: ConnectionChecker) {
    operator fun invoke() = connectionChecker.isInternetAvailable()
}