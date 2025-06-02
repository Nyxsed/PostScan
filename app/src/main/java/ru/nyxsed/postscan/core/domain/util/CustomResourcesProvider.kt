package ru.nyxsed.postscan.core.domain.util

import androidx.annotation.StringRes

interface CustomResourcesProvider {
    fun getString(@StringRes resId: Int): String
}