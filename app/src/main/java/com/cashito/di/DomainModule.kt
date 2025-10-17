package com.cashito.di

import com.cashito.domain.usecases.auth.GetCurrentUserUseCase
import com.cashito.domain.usecases.auth.LoginUseCase
import com.cashito.domain.usecases.auth.RegisterUseCase
import com.cashito.domain.usecases.expense.AddExpenseUseCase
import com.cashito.domain.usecases.income.AddIncomeUseCase
import com.cashito.domain.usecases.transaction.GetTransactionsUseCase
import org.koin.dsl.module
import com.cashito.domain.usecases.reports.ObserveExpenseReportUseCase
import com.cashito.domain.usecases.balance.GetBalanceUseCase
import com.cashito.domain.usecases.transaction.DeleteTransactionUseCase
import com.cashito.domain.usecases.transaction.GetTransactionByIdUseCase
import com.cashito.domain.usecases.transaction.UpdateTransactionUseCase

val domainModule = module {

    // Auth
    factory { RegisterUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }

    // Income & Expense
    factory { AddIncomeUseCase(get()) }
    factory { AddExpenseUseCase(get()) }

    // Transaction
    factory { GetTransactionsUseCase(get(), get()) }
    factory { ObserveExpenseReportUseCase(get()) }
    factory { GetTransactionByIdUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }

    factory { GetBalanceUseCase(get(), get()) } // AÑADIDO

}
