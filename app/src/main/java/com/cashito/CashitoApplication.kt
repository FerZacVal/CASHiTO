package com.cashito

import android.app.Application
import com.cashito.di.dataModule
import com.cashito.di.domainModule
import com.cashito.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CashitoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CashitoApplication)
            // Cargas todos los módulos de Koin que definen cómo construir tus dependencias.
            modules(dataModule, domainModule, viewModelModule)
        }
    }
}
