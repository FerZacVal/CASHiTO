package com.cashito.di

import com.cashito.ui.viewmodel.CreateUserViewModel
import com.cashito.ui.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Define cómo crear el LoginViewModel. Koin le pasará automáticamente
    // el LoginUseCase que necesita (get()), que ya definimos en el domainModule.
    viewModel { LoginViewModel(get()) }

    // Define cómo crear el CreateUserViewModel. Koin le pasará automáticamente
    // el RegisterUseCase que necesita (get()), que ya definimos en el domainModule.
    viewModel { CreateUserViewModel(get()) }

    // Aquí añadiríamos otros ViewModels a medida que los creemos
}
