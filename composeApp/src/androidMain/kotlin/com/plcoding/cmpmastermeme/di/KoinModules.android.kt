package com.plcoding.cmpmastermeme.di

import com.plcoding.cmpmastermeme.core.database.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val coreDataPlatformModule = module {
    single { DatabaseFactory(androidContext()) }
}