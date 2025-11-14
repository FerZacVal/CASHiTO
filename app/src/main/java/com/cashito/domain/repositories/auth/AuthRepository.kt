package com.cashito.domain.repositories.auth

import com.cashito.domain.entities.auth.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): User
    suspend fun register(email: String, password: String, nombre: String): User
    suspend fun logout()
    fun getAuthState(): Flow<User?>
    suspend fun getCurrentUser(): User?

    // --- Métodos para la gestión de credenciales ---
    suspend fun saveCredentials(email: String, password: String)
    suspend fun getSavedCredentials(): Pair<String, String>?
    suspend fun clearCredentials()
    suspend fun hasSavedCredentials(): Boolean
}

// --- ERROR HANDLING ---

class AuthException(val error: AuthError) : Exception(error.message)

sealed class AuthError(val message: String) {
    data object UserAlreadyExists : AuthError("El correo electrónico ya está en uso.")
    data object InvalidCredentials : AuthError("Las credenciales son incorrectas.")
    data class Unknown(val reason: String?) : AuthError("Ha ocurrido un error inesperado: ${reason ?: "Desconocido"}")
}
