package ru.nyxsed.postscan.features.preferences.data.util

import android.content.Context
import ru.nyxsed.postscan.features.preferences.domain.util.CustomResourcesProvider

class AndroidCustomResourcesProvider(private val context: Context) : CustomResourcesProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}