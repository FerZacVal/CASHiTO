package com.cashito.domain.repositories.auth

import com.cashito.domain.entities.auth.User
import kotlinx.coroutines.flow.Flow

/**
 * Define un contrato para las operaciones de autenticación.
 * La capa de datos implementará esta interfaz.
 */
interface AuthRepository {

    /**
     * Inicia sesión con email y contraseña.
     * @return El [User] si el login es exitoso.
     * @throws AuthException si ocurre un error (ej. credenciales inválidas).
     */
    suspend fun login(email: String, password: String): User

    /**
     * Registra un nuevo usuario con email y contraseña.
     * @return El [User] si el registro es exitoso.
     * @throws AuthException si ocurre un error (ej. el usuario ya existe).
     */
    suspend fun register(email: String, password: String): User

    /**
     * Cierra la sesión del usuario actual.
     */
    suspend fun logout()

    /**
     * Devuelve un Flow que emite el estado de autenticación actual.
     * Emite el [User] si hay una sesión activa, o null si no la hay.
     * Es ideal para observar cambios de estado en tiempo real.
     */
    fun getAuthState(): Flow<User?>
}

/**
 * Representa los posibles errores específicos de la autenticación.
 */
sealed class AuthError {
    object InvalidCredentials : AuthError()
    object UserAlreadyExists : AuthError()
    object NetworkError : AuthError()
    data class Unknown(val message: String?) : AuthError()
}

/**
 * Excepción personalizada para encapsular los [AuthError].
 * La capa de datos lanzará esta excepción para que el caso de uso la atrape.
 */
class AuthException(val error: AuthError) : Exception()