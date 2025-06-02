package ru.nyxsed.postscan.core.domain.util

interface ConnectionChecker {
    fun isInternetAvailable(): Boolean
    fun isTokenValid(): Boolean
}