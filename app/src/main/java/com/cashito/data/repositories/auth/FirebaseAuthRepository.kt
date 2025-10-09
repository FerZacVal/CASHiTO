package com.cashito.data.repositories.auth

import com.cashito.data.datasources.firebase.FirebaseAuthDataSource
import com.cashito.data.mappers.FirebaseUserMapper
import com.cashito.domain.entities.auth.User
import com.cashito.domain.repositories.auth.AuthError
import com.cashito.domain.repositories.auth.AuthException
import com.cashito.domain.repositories.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseAuthRepository(
    private val dataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): User {
        try {
            // CORRECTED: Call signIn instead of login
            val firebaseUser = dataSource.signIn(email, password)
            return FirebaseUserMapper.map(firebaseUser)!!
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw AuthException(AuthError.InvalidCredentials)
        } catch (e: FirebaseAuthException) {
            throw AuthException(AuthError.Unknown(e.message))
        }
    }

    override suspend fun register(email: String, password: String): User {
        try {
            // CORRECTED: Call createUser instead of register
            val firebaseUser = dataSource.createUser(email, password)
            return FirebaseUserMapper.map(firebaseUser)!!
        } catch (e: FirebaseAuthUserCollisionException) {
            throw AuthException(AuthError.UserAlreadyExists)
        } catch (e: FirebaseAuthException) {
            throw AuthException(AuthError.Unknown(e.message))
        }
    }

    override suspend fun logout() {
        // CORRECTED: Call signOut instead of logout
        dataSource.signOut()
    }

    override fun getAuthState(): Flow<User?> {
        // CORRECTED: Use the getAuthState flow from the dataSource and map the result
        return dataSource.getAuthState().map { firebaseUser ->
            FirebaseUserMapper.map(firebaseUser)
        }
    }
}
