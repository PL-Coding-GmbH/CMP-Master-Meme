package com.plcoding.cmpmastermeme.di

import com.plcoding.cmpmastermeme.editmeme.data.CacheSaveStrategy
import com.plcoding.cmpmastermeme.editmeme.data.MemeExporter
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy
import com.plcoding.cmpmastermeme.editmeme.presentation.util.ShareSheetManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreDataPlatformModule = module {
    single { MemeExporter(context = androidContext()) }

    factory {
        CacheSaveStrategy(context = androidContext())
    }.bind<SaveToStorageStrategy>()

    factory {
        ShareSheetManager(context = androidContext())
    }.bind<ShareSheetManager>()
}