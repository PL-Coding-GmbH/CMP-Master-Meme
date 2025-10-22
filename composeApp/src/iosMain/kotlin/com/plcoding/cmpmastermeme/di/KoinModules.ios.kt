package com.plcoding.cmpmastermeme.di

import com.plcoding.cmpmastermeme.editmeme.data.CacheSaveStrategy
import com.plcoding.cmpmastermeme.editmeme.data.MemeExporter
import com.plcoding.cmpmastermeme.editmeme.domain.SaveToStorageStrategy
import com.plcoding.cmpmastermeme.editmeme.presentation.util.ShareSheetManager
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreDataPlatformModule = module {
    singleOf(::MemeExporter)
    factoryOf(::CacheSaveStrategy).bind<SaveToStorageStrategy>()
    factoryOf(::ShareSheetManager)
}