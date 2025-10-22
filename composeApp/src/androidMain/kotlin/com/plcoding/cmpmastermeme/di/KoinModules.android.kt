package com.plcoding.cmpmastermeme.di

import com.plcoding.cmpmastermeme.editmeme.domain.FilePathResolver
import com.plcoding.cmpmastermeme.editmeme.data.MemeExporter
import com.plcoding.cmpmastermeme.editmeme.data.PlatformFilePathResolver
import com.plcoding.cmpmastermeme.editmeme.presentation.util.ShareSheetManager
import com.plcoding.cmpmastermeme.editmeme.data.CacheSaveStrategy
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreDataPlatformModule = module {
    single { MemeExporter(androidContext()) }
    factory(named("cache")) {
        CacheSaveStrategy(context = androidContext())
    }.bind<SaveToStorageStrategy>()

    factory {
        ShareSheetManager(context = androidContext())
    }.bind<ShareSheetManager>()
    single {
        PlatformFilePathResolver(context = androidContext())
    }.bind<FilePathResolver>()
}