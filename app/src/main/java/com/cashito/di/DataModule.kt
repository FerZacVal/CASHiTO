package com.cashito.di

import com.cashito.data.datasources.firebase.FirebaseAuthDataSource
import com.cashito.data.repositories.auth.AuthRepositoryImpl
import com.cashito.domain.repositories.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

val dataModule = module {

    // Provee una instancia única (singleton) de FirebaseAuth
    single { FirebaseAuth.getInstance() }

    // Provee una instancia del DataSource. Koin automáticamente le pasará
    // la instancia de FirebaseAuth que definimos en la línea anterior (get()).
    single { FirebaseAuthDataSource(get()) }

    // Provee la implementación del repositorio. Cuando el dominio pida un `AuthRepository`,
    // Koin le dará un `AuthRepositoryImpl`, pasándole el DataSource que ya sabe crear (get()).
    single<AuthRepository> { AuthRepositoryImpl(get()) }

}
