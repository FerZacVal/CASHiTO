package com.cashito.di

import com.cashito.data.datasources.firebase.FirebaseAuthDataSource
import com.cashito.data.repositories.AuthRepositoryImpl
import com.cashito.domain.repositories.AuthRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    // Provee una instancia única (singleton) de FirebaseAuthDataSource
    single { FirebaseAuthDataSource() }

    // Provee una nueva instancia de AuthRepositoryImpl cada vez que se solicita,
    // y la enlaza a la interfaz AuthRepository del dominio.
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
}
