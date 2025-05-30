package ru.nyxsed.postscan.features.preferences.domain.repository

import android.net.Uri

interface DatabaseRepository {
    suspend fun export(uri: Uri): Boolean
    suspend fun import(uri: Uri): Boolean
}