package com.cashito.di

import com.cashito.domain.repositories.auth.AuthRepository
import com.cashito.domain.repositories.expense.ExpenseRepository
import com.cashito.domain.repositories.income.IncomeRepository
import com.cashito.domain.usecases.auth.GetAuthStateUseCase
import com.cashito.domain.usecases.auth.LoginUseCase
import com.cashito.domain.usecases.auth.LogoutUseCase
import com.cashito.domain.usecases.auth.RegisterUseCase
import com.cashito.domain.usecases.expense.AddExpenseUseCase
import com.cashito.domain.usecases.income.AddIncomeUseCase
import org.koin.dsl.module

val domainModule = module {
    // --- Auth UseCases ---
    factory { RegisterUseCase(get<AuthRepository>()) }
    factory { LoginUseCase(get<AuthRepository>()) }
    factory { GetAuthStateUseCase(get<AuthRepository>()) }
    factory { LogoutUseCase(get<AuthRepository>()) }

    // --- Expense UseCases ---
    // Koin ahora sabe que AddExpenseUseCase necesita un ExpenseRepository
    // que se encuentra en com.cashito.domain.repositories.expense
    factory { AddExpenseUseCase(get<ExpenseRepository>()) }

    // --- Income UseCases ---
    // Koin ahora sabe que AddIncomeUseCase necesita un IncomeRepository
    // que se encuentra en com.cashito.domain.repositories.income
    factory { AddIncomeUseCase(get<IncomeRepository>()) }
}
