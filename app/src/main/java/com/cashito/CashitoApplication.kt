package com.cashito

import android.app.Application
import com.cashito.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CashitoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CashitoApplication)
            modules(appModule)//carpeta di. loginusecase loginviewmodel y auth repository
    }
    }
}
