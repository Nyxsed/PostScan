package ru.nyxsed.postscan.common.domain.util

import androidx.annotation.StringRes

interface CustomResourcesProvider {
    fun getString(@StringRes resId: Int): String
}