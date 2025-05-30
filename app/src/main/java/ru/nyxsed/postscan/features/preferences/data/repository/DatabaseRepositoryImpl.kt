package ru.nyxsed.postscan.features.preferences.data.repository

import android.content.Context
import android.net.Uri
import ru.nyxsed.postscan.data.repository.DbRepository
import ru.nyxsed.postscan.features.preferences.domain.repository.DatabaseRepository

class DatabaseRepositoryImpl(
    private val dbRepository: DbRepository,
    private val context: Context
) : DatabaseRepository {
    override suspend fun export(uri: Uri): Boolean = dbRepository.exportDatabase(context, uri)
    override suspend fun import(uri: Uri): Boolean = dbRepository.importDatabase(context, uri)
}