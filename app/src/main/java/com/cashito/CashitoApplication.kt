package com.cashito

import android.app.Application
import com.cashito.di.dataModule
import com.cashito.di.domainModule
import com.cashito.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CashitoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@CashitoApplication)
            modules(dataModule, domainModule, uiModule)
        }
    }
}