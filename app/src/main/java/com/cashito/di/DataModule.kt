package com.cashito.di

import com.cashito.data.datasources.firebase.FirebaseAuthDataSource
import com.cashito.data.datasources.firebase.FirebaseTransactionDataSource
import com.cashito.data.repositories.AuthRepositoryImpl
import com.cashito.data.repositories.ExpenseRepositoryImpl
import com.cashito.data.repositories.IncomeRepositoryImpl
import com.cashito.domain.repositories.auth.AuthRepository
import com.cashito.domain.repositories.expense.ExpenseRepository
import com.cashito.domain.repositories.income.IncomeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

val dataModule = module {

    // --- Firebase Instances ---
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // --- DataSources ---
    // CORRECCIÓN: FirebaseAuthDataSource solo necesita UNA dependencia (FirebaseAuth).
    single { FirebaseAuthDataSource(get()) }
    // FirebaseTransactionDataSource necesita DOS dependencias (Firestore y Auth).
    single { FirebaseTransactionDataSource(get(), get()) }

    // --- Repositories ---
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get()) }
    single<IncomeRepository> { IncomeRepositoryImpl(get()) }

}
