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
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreDataPlatformModule = module {
    singleOf(::DatabaseFactory)
    singleOf(::MemeExporter)
    factory(named("cache")) {
        CacheSaveStrategy()
    }.bind<SaveToStorageStrategy>()
    factory(named("private_dir")) {
        PrivateAppDirSaveStrategy()
    }.bind<SaveToStorageStrategy>()
    factory {
        PlatformSendableFileManager()
    }.bind<SendableFileManager>()
    single {
        PlatformFilePathResolver()
    }.bind<FilePathResolver>()
}