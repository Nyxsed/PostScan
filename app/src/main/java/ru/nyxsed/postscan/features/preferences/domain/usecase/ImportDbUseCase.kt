package ru.nyxsed.postscan.features.preferences.domain.usecase

import android.net.Uri
import ru.nyxsed.postscan.features.preferences.domain.repository.DatabaseRepository

class ImportDbUseCase(private val repo: DatabaseRepository) {
    suspend operator fun invoke(uri: Uri): Boolean = repo.import(uri)
}