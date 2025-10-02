package com.plcoding.cmpmastermeme.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.plcoding.cmpmastermeme.SharedApplicationScope
import com.plcoding.cmpmastermeme.core.database.DatabaseFactory
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

object KoinModules {
    private val appModule = module {
        single<CoroutineScope> { SharedApplicationScope.scope }
    }

    private val databaseModule = module {
        single {
            get<DatabaseFactory>().create()
                .setDriver(
                    BundledSQLiteDriver()
                )
                .build()
        }
    }

    val allModules = listOf(
        appModule,
        databaseModule
    )
}