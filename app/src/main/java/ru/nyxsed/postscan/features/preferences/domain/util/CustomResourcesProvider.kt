package ru.nyxsed.postscan.features.preferences.domain.util

import androidx.annotation.StringRes

interface CustomResourcesProvider {
    fun getString(@StringRes resId: Int): String
}