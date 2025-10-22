package com.plcoding.cmpmastermeme.di

import com.plcoding.cmpmastermeme.SharedApplicationScope
import com.plcoding.cmpmastermeme.editmeme.presentation.EditMemeViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val coreDataPlatformModule: Module

object KoinModules {
    private val appModule = module {
        single<CoroutineScope> { SharedApplicationScope.scope }
    }

    private val memeModule = module {
        viewModelOf(::EditMemeViewModel)
    }

    val allModules = listOf(
        appModule,
        coreDataPlatformModule,
        memeModule
    )
}