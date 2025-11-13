package com.cashito.di

import com.cashito.ui.viewmodel.BalanceViewModel
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
import com.cashito.ui.viewmodel.SplashViewModel
import com.cashito.ui.viewmodel.TransactionEditViewModel
import com.cashito.ui.viewmodel.TransactionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    // --- Splash ---
    viewModel { SplashViewModel(get()) } // AÑADIDO

    // --- Auth ViewModels (necesitan UseCase) ---
    viewModel { LoginViewModel(get(), get()) }
    viewModel { CreateUserViewModel(get()) }

    // --- Transaction ViewModels (necesitan UseCase y/o SavedStateHandle) ---
    viewModel { QuickOutViewModel(get(), get()) }
    viewModel { QuickSaveViewModel(get(), get()) }
    viewModel { TransactionsViewModel(get(), get()) }
    viewModel { TransactionEditViewModel(get(), get(), get()) }

    // --- Goal ViewModels (necesitan SavedStateHandle) ---
    viewModel { GoalsViewModel(get()) }
    viewModel { GoalDetailViewModel(get()) }
    viewModel { GoalFormViewModel(get(), get()) }

    // --- Dashboard & Profile ---
    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get()) }

    // --- ViewModels sin dependencias ---
    viewModel { CategoryFormViewModel() }
    viewModel { ReportsViewModel(get()) }
    viewModel { CategoryExpenseReportViewModel(get()) }
    viewModel { IncomeReportViewModel(get()) }
    viewModel { BalanceViewModel(get()) }

}
