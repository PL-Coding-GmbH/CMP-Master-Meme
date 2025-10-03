package com.plcoding.cmpmastermeme.di

import com.plcoding.cmpmastermeme.core.database.DatabaseFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val coreDataPlatformModule = module {
    singleOf(::DatabaseFactory)
}