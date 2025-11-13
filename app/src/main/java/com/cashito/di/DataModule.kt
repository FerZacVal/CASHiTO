package com.cashito.di

import androidx.room.Room
import com.cashito.data.datasources.firebase.FirebaseAuthDataSource
import com.cashito.data.datasources.firebase.FirebaseTransactionDataSource
import com.cashito.data.datasources.firebase.GoalDataSource
import com.cashito.data.datasources.local.CashitoDatabase
import com.cashito.data.repositories.AuthRepositoryImpl
import com.cashito.data.repositories.ExpenseRepositoryImpl
import com.cashito.data.repositories.GoalRepositoryImpl
import com.cashito.data.repositories.IncomeRepositoryImpl
import com.cashito.data.repositories.TransactionRepositoryImpl
import com.cashito.domain.repositories.auth.AuthRepository
import com.cashito.domain.repositories.expense.ExpenseRepository
import com.cashito.domain.repositories.goal.GoalRepository
import com.cashito.domain.repositories.income.IncomeRepository
import com.cashito.domain.repositories.transaction.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    // --- Room Database ---
    single {
        Room.databaseBuilder(
            androidContext(),
            CashitoDatabase::class.java,
            CashitoDatabase.DATABASE_NAME
        ).build()
    }

    // --- DAOs ---
    single { get<CashitoDatabase>().userCredentialsDao() }

    // --- Firebase Instances ---
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // --- DataSources ---
    single { FirebaseAuthDataSource(get()) }
    single { FirebaseTransactionDataSource(get(), get()) }
    single { GoalDataSource(get(), get()) }

    // --- Repositories ---
    // AuthRepositoryImpl ahora recibe el DAO de Room además de su dependencia de Firebase
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get()) }
    single<IncomeRepository> { IncomeRepositoryImpl(get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<GoalRepository> { GoalRepositoryImpl(get()) }
}
