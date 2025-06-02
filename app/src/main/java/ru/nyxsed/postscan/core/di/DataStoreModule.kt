package ru.nyxsed.postscan.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module

val dataStoreModule = module {
    single<DataStore<Preferences>> { get<Context>().dataStore }
}