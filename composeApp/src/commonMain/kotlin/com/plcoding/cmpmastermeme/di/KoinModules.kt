package com.plcoding.cmpmastermeme.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.plcoding.cmpmastermeme.SharedApplicationScope
import com.plcoding.cmpmastermeme.core.data.RoomLocalMemeDataSource
import com.plcoding.cmpmastermeme.core.database.DatabaseFactory
import com.plcoding.cmpmastermeme.core.database.MasterMemeDatabase
import com.plcoding.cmpmastermeme.core.domain.LocalMemeDataSource
import com.plcoding.cmpmastermeme.editmeme.EditMemeViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val coreDataPlatformModule: Module

object KoinModules {
    private val appModule = module {
        single<CoroutineScope> { SharedApplicationScope.scope }
    }

    private val coreDataModule = module {
        factoryOf(::RoomLocalMemeDataSource).bind<LocalMemeDataSource>()
    }

    private val databaseModule = module {
        single<MasterMemeDatabase> {
            get<DatabaseFactory>().create()
                .setDriver(
                    BundledSQLiteDriver()
                )
                .build()
        }
    }

    private val editMemeModule = module {
        viewModelOf(::EditMemeViewModel)
    }

    val allModules = listOf(
        appModule,
        coreDataModule,
        coreDataPlatformModule,
        databaseModule,
        editMemeModule
    )
}