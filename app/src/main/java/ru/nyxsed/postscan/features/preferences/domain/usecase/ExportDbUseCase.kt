package ru.nyxsed.postscan.features.preferences.domain.usecase

import android.net.Uri
import ru.nyxsed.postscan.core.domain.repository.DbRepository

class ExportDbUseCase(private val repo: DbRepository) {
    suspend operator fun invoke(uri: Uri): Boolean = repo.exportDatabase(uri)
}