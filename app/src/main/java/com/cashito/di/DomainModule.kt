package com.cashito.di

import com.cashito.domain.usecases.auth.GetAuthStateUseCase
import com.cashito.domain.usecases.auth.LoginUseCase
import com.cashito.domain.usecases.auth.LogoutUseCase
import com.cashito.domain.usecases.auth.RegisterUseCase
import org.koin.dsl.module

val domainModule = module {
    // Define cómo crear cada caso de uso. Koin automáticamente les proveerá
    // el `AuthRepository` que definimos en el dataModule (get()).
    factory { RegisterUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { GetAuthStateUseCase(get()) }
    factory { LogoutUseCase(get()) }
}
