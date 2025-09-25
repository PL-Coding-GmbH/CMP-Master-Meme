package com.plcoding.cmpmastermeme.di

import com.plcoding.cmpmastermeme.SharedApplicationScope
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.dsl.module

object KoinModules {
    private val appModule = module {
        single<CoroutineScope> { SharedApplicationScope.scope }
    }

    val allModules = listOf(
        appModule
    )
}