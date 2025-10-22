package com.plcoding.cmpmastermeme.di

import com.plcoding.cmpmastermeme.editmeme.data.CacheSaveStrategy
import com.plcoding.cmpmastermeme.editmeme.domain.FilePathResolver
import com.plcoding.cmpmastermeme.editmeme.data.MemeExporter
import com.plcoding.cmpmastermeme.editmeme.data.PlatformFilePathResolver
import com.plcoding.cmpmastermeme.editmeme.presentation.util.ShareSheetManager
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreDataPlatformModule = module {
    singleOf(::MemeExporter)
    singleOf(::PlatformFilePathResolver).bind<FilePathResolver>()
    factoryOf(::CacheSaveStrategy).bind<SaveStrategy>()
    factoryOf(::ShareSheetManager)
}