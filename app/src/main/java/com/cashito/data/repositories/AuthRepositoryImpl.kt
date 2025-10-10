package com.cashito.data.repositories

// Forcing the correct import path
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

    override suspend fun register(email: String, password: String, nombre: String): User {
        try {
            // Step 1: Create user in Firebase Authentication
            val firebaseUser = firebaseAuthDataSource.createUser(email, password)
            
            // Step 2: Save user profile to Firestore, now with the name
            firebaseAuthDataSource.saveUserProfile(firebaseUser, nombre)

            // Step 3: Return the domain user object
            return firebaseUser.toDomain()
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
