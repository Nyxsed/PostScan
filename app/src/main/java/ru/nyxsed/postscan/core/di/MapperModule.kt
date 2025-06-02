package ru.nyxsed.postscan.core.di

import org.koin.dsl.module
import ru.nyxsed.postscan.core.data.mapper.VkMapper

val mapperModule = module {
    single {
        VkMapper()
    }
}