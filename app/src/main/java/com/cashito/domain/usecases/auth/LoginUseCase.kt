package com.cashito.domain.usecases.auth

import com.cashito.domain.entities.auth.User
import com.cashito.domain.repositories.auth.AuthException
import com.cashito.domain.repositories.auth.AuthRepository

/**
 * Caso de uso para iniciar sesión de un usuario.
 */
class LoginUseCase(private val authRepository: AuthRepository) {

    /**
     * Ejecuta el caso de uso para el login.
     *
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     * @return Un [Result] que encapsula el [User] en caso de éxito o la [AuthException] en caso de fallo.
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return try {
            val user = authRepository.login(email, password)
            Result.success(user)
        } catch (e: AuthException) {
            Result.failure(e)
        } catch (e: Exception) {
            // Captura de cualquier otro error inesperado
            Result.failure(e)
        }
    }
}