package com.cashito.di

import com.cashito.ui.viewmodel.CreateUserViewModel
import com.cashito.ui.viewmodel.LoginViewModel
import com.cashito.ui.viewmodel.QuickOutViewModel
import com.cashito.ui.viewmodel.QuickSaveViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // --- Auth ViewModels ---
    viewModel { LoginViewModel(get()) }
    viewModel { CreateUserViewModel(get()) }

    // --- Transaction ViewModels ---
    viewModel { QuickOutViewModel(get()) }
    viewModel { QuickSaveViewModel(get()) }

    // Aquí añadiríamos otros ViewModels a medida que los creemos
}
