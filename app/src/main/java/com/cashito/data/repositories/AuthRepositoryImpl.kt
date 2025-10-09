package com.cashito.data.repositories

import com.cashito.data.datasources.firebase.FirebaseAuthDataSource
import com.cashito.data.mappers.toDomain
import com.cashito.domain.entities.auth.User
import com.cashito.domain.repositories.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): User {
        return firebaseAuthDataSource.signIn(email, password).toDomain()
    }

    override suspend fun register(email: String, password: String): User {
        return firebaseAuthDataSource.createUser(email, password).toDomain()
    }

    override fun getAuthState(): Flow<User?> {
        return firebaseAuthDataSource.getAuthState().map { firebaseUser ->
            firebaseUser?.toDomain()
        }
    }

    override suspend fun logout() {
        firebaseAuthDataSource.signOut()
    }
}
