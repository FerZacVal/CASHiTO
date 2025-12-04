package com.cashito.di

import com.cashito.ui.viewmodel.BalanceViewModel
import com.cashito.ui.viewmodel.CategoriesViewModel
import com.cashito.ui.viewmodel.CategoryEditViewModel
import com.cashito.ui.viewmodel.CategoryExpenseReportViewModel
import com.cashito.ui.viewmodel.CategoryFormViewModel
import com.cashito.ui.viewmodel.CreateUserViewModel
import com.cashito.ui.viewmodel.DashboardViewModel
import com.cashito.ui.viewmodel.GoalDetailViewModel
import com.cashito.ui.viewmodel.GoalFormViewModel
import com.cashito.ui.viewmodel.GoalsViewModel
import com.cashito.ui.viewmodel.IncomeReportViewModel
import com.cashito.ui.viewmodel.LoginViewModel
import com.cashito.ui.viewmodel.ProfileViewModel
import com.cashito.ui.viewmodel.QuickOutViewModel
import com.cashito.ui.viewmodel.QuickSaveViewModel
import com.cashito.ui.viewmodel.ReportsViewModel
import com.cashito.ui.viewmodel.RewardsViewModel
import com.cashito.ui.viewmodel.SplashViewModel
import com.cashito.ui.viewmodel.TransactionEditViewModel
import com.cashito.ui.viewmodel.TransactionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    // --- Splash ---
    viewModel { SplashViewModel(get()) }

    // --- Auth ViewModels ---
    viewModel { LoginViewModel(get(), get()) }
    viewModel { CreateUserViewModel(get()) }

    // --- Transaction ViewModels ---
    viewModel { QuickOutViewModel(get(), get(), get()) }
    viewModel { QuickSaveViewModel(get(), get(), get()) }
    viewModel { TransactionsViewModel(get(), get()) }
    viewModel { TransactionEditViewModel(get(), get(), get()) }

    // --- Goal ViewModels ---
    viewModel { GoalsViewModel(get()) }
    viewModel { GoalDetailViewModel(get(), get(), get(), get()) }
    // ARREGLADO: Se inyectan los tres casos de uso necesarios para crear, leer y actualizar metas.
    viewModel { GoalFormViewModel(get(), get(), get(), get()) }

    // --- Dashboard & Profile ---
    viewModel { DashboardViewModel(get(), get(), get(), get()) } // Updated to inject GetWeeklyChallengeUseCase
    viewModel { ProfileViewModel(get()) }

    // --- Category ViewModels ---
    viewModel { CategoryFormViewModel(get()) }
    viewModel { CategoriesViewModel(get()) }
    viewModel { CategoryEditViewModel(get(), get(), get()) } // ACTUALIZADO

    // --- Reports & Balance ViewModels ---
    viewModel { ReportsViewModel(get()) }
    viewModel { CategoryExpenseReportViewModel(get()) }
    viewModel { IncomeReportViewModel(get()) }
    viewModel { BalanceViewModel(get()) }
    
    // --- Gamification ---
    viewModel { RewardsViewModel(get(), get(), get(), get(), get()) }
}
