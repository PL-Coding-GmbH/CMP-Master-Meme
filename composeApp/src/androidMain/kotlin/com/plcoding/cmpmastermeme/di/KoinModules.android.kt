package com.plcoding.cmpmastermeme.di

import com.plcoding.cmpmastermeme.core.database.DatabaseFactory
import com.plcoding.cmpmastermeme.core.domain.CacheSaveStrategy
import com.plcoding.cmpmastermeme.core.domain.FilePathResolver
import com.plcoding.cmpmastermeme.core.domain.MemeExporter
import com.plcoding.cmpmastermeme.core.domain.PlatformFilePathResolver
import com.plcoding.cmpmastermeme.core.domain.PlatformSendableFileManager
import com.plcoding.cmpmastermeme.core.domain.PrivateAppDirSaveStrategy
import com.plcoding.cmpmastermeme.core.domain.SaveToStorageStrategy
import com.plcoding.cmpmastermeme.core.domain.SendableFileManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreDataPlatformModule = module {
    single { DatabaseFactory(androidContext()) }
    // TODO this isnt core deps below - move them
    single { MemeExporter(androidContext()) }
    factory(named("cache")) {
        CacheSaveStrategy(context = androidContext())
    }.bind<SaveToStorageStrategy>()
    factory(named("private_dir")) {
        PrivateAppDirSaveStrategy(context = androidContext())
    }.bind<SaveToStorageStrategy>()
    factory {
        PlatformSendableFileManager(context = androidContext())
    }.bind<SendableFileManager>()
    single {
        PlatformFilePathResolver(context = androidContext())
    }.bind<FilePathResolver>()
}