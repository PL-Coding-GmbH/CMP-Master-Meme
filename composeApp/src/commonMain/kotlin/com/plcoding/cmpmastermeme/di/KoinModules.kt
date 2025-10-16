package com.plcoding.cmpmastermeme.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.plcoding.cmpmastermeme.SharedApplicationScope
import com.plcoding.cmpmastermeme.core.data.LocalMemeDataSource
import com.plcoding.cmpmastermeme.core.database.DatabaseFactory
import com.plcoding.cmpmastermeme.core.database.MasterMemeDatabase
import com.plcoding.cmpmastermeme.core.domain.MemeDataSource
import com.plcoding.cmpmastermeme.editmeme.EditMemeViewModel
import com.plcoding.cmpmastermeme.memelist.MemeListViewModel
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
        factoryOf(::LocalMemeDataSource).bind<MemeDataSource>()
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

    private val memeModule = module {
        viewModelOf(::EditMemeViewModel)
        viewModelOf(::MemeListViewModel)
    }

    val allModules = listOf(
        appModule,
        coreDataModule,
        coreDataPlatformModule,
        databaseModule,
        memeModule
    )
}