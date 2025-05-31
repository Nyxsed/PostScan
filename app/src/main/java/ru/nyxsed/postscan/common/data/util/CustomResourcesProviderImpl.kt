package ru.nyxsed.postscan.common.data.util

import android.content.Context
import ru.nyxsed.postscan.common.domain.util.CustomResourcesProvider

class CustomResourcesProviderImpl(private val context: Context) : CustomResourcesProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}