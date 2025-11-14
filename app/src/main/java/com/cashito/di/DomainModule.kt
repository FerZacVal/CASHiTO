package com.cashito.di

import com.cashito.domain.usecases.auth.AutoLoginUseCase
import com.cashito.domain.usecases.auth.GetCurrentUserUseCase
import com.cashito.domain.usecases.auth.LoginUseCase
import com.cashito.domain.usecases.auth.RegisterUseCase
import com.cashito.domain.usecases.balance.GetBalanceUseCase
import com.cashito.domain.usecases.category.GetCategoryByIdUseCase
import com.cashito.domain.usecases.category.UpdateCategoryUseCase
import com.cashito.domain.usecases.expense.AddExpenseUseCase
import com.cashito.domain.usecases.goal.CreateGoalUseCase
import com.cashito.domain.usecases.goal.GetGoalsUseCase
import com.cashito.domain.usecases.income.AddIncomeUseCase
import com.cashito.domain.usecases.reports.ObserveExpenseReportUseCase
import com.cashito.domain.usecases.reports.ObserveIncomeReportUseCase
import com.cashito.domain.usecases.reports.ObserveReportsUseCase
import com.cashito.domain.usecases.transaction.DeleteTransactionUseCase
import com.cashito.domain.usecases.transaction.GetTransactionByIdUseCase
import com.cashito.domain.usecases.transaction.GetTransactionsUseCase
import com.cashito.domain.usecases.transaction.UpdateTransactionUseCase
import org.koin.dsl.module

val domainModule = module {

    // Auth
    factory { RegisterUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { AutoLoginUseCase(get(), get()) }

    // Income & Expense
    factory { AddIncomeUseCase(get()) }
    factory { AddExpenseUseCase(get()) }

    // Goal
    factory { CreateGoalUseCase(get()) }
    factory { GetGoalsUseCase(get()) }

    // Transaction
    factory { GetTransactionsUseCase(get(), get()) }
    factory { GetTransactionByIdUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }

    // Balance
    factory { GetBalanceUseCase(get(), get()) }

    // Category
    factory { GetCategoryByIdUseCase(get()) }
    factory { UpdateCategoryUseCase(get()) } // ACTUALIZADO

    // Reports
    factory { ObserveReportsUseCase(get(), get()) }
    factory { ObserveIncomeReportUseCase(get()) }
    factory { ObserveExpenseReportUseCase(get()) }
}
