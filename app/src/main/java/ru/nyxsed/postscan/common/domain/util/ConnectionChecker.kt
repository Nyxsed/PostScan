package ru.nyxsed.postscan.common.domain.util

interface ConnectionChecker {
    fun isInternetAvailable(): Boolean
    fun isTokenValid(): Boolean
}