package com.cashito.data.repositories.auth

import com.cashito.data.datasources.firebase.FirebaseAuthDataSource
import com.cashito.data.mappers.FirebaseUserMapper
import com.cashito.domain.entities.auth.User
import com.cashito.domain.repositories.auth.AuthError
import com.cashito.domain.repositories.auth.AuthException
import com.cashito.domain.repositories.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthException

class FirebaseAuthRepository(
    private val dataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): User {
        try {
            val firebaseUser = dataSource.login(email, password)
                ?: throw AuthException(AuthError.InvalidCredentials)
            return FirebaseUserMapper.map(firebaseUser)!!
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthException(AuthError.InvalidCredentials)
        } catch (e: FirebaseAuthException) {
            throw AuthException(AuthError.Unknown(e.message))
        }
    }

    override suspend fun register(email: String, password: String): User {
        try {
            val firebaseUser = dataSource.register(email, password)
                ?: throw AuthException(AuthError.Unknown("No se pudo registrar"))
            return FirebaseUserMapper.map(firebaseUser)!!
        } catch (e: FirebaseAuthUserCollisionException) {
            throw AuthException(AuthError.UserAlreadyExists)
        } catch (e: FirebaseAuthException) {
            throw AuthException(AuthError.Unknown(e.message))
        }
    }

    override suspend fun logout() {
        dataSource.logout()
    }

    override fun getAuthState(): Flow<User?> = flow {
        val firebaseUser = dataSource.getCurrentUser()
        emit(FirebaseUserMapper.map(firebaseUser))
    }
}
