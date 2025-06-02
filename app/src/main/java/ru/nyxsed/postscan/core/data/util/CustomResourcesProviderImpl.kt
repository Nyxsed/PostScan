package ru.nyxsed.postscan.core.data.util

import android.content.Context
import ru.nyxsed.postscan.core.domain.util.CustomResourcesProvider

class CustomResourcesProviderImpl(private val context: Context) : CustomResourcesProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}