package com.plcoding.cmpmastermeme

import android.app.Application
import com.plcoding.cmpmastermeme.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MasterMemeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MasterMemeApplication)
            androidLogger()
        }
    }
}