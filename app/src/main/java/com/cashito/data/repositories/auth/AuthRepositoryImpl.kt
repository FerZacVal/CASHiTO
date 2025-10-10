package com.cashito.data.repositories.auth

import com.cashito.data.datasources.firebase.FirebaseAuthDataSource
import com.cashito.data.mappers.toDomain
import com.cashito.domain.entities.auth.User
import com.cashito.domain.repositories.auth.AuthError
import com.cashito.domain.repositories.auth.AuthException
import com.cashito.domain.repositories.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): User {
        try {
            return firebaseAuthDataSource.signIn(email, password).toDomain()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthException(AuthError.InvalidCredentials)
        } catch (e: Exception) {
            throw AuthException(AuthError.Unknown(e.message))
        }
    }

    override suspend fun register(email: String, password: String): User {
        try {
            return firebaseAuthDataSource.createUser(email, password).toDomain()
        } catch (e: FirebaseAuthUserCollisionException) {
            throw AuthException(AuthError.UserAlreadyExists)
        } catch (e: Exception) {
            throw AuthException(AuthError.Unknown(e.message))
        }
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