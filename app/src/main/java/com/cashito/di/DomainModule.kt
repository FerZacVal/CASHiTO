package com.cashito.di

import com.cashito.domain.usecases.auth.LoginUseCase
import com.cashito.domain.usecases.auth.RegisterUseCase
import com.cashito.domain.usecases.expense.AddExpenseUseCase
import com.cashito.domain.usecases.income.AddIncomeUseCase
import com.cashito.domain.usecases.transaction.GetTransactionsUseCase
import org.koin.dsl.module

val domainModule = module {

    // Auth
    factory { RegisterUseCase(get()) }
    factory { LoginUseCase(get()) } // <-- ESTA LÍNEA FALTABA

    // Income & Expense
    factory { AddIncomeUseCase(get()) }
    factory { AddExpenseUseCase(get()) }

    // Transaction
    factory { GetTransactionsUseCase(get(), get()) }

}
