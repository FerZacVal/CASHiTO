package com.cashito.data.repositories

import com.cashito.data.datasources.firebase.FirebaseAuthDataSource
import com.cashito.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuthDataSource.signIn(email, password)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        firebaseAuthDataSource.signOut()
    }
}
