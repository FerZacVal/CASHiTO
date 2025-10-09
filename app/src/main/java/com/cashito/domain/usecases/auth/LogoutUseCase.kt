package com.cashito.domain.usecases.auth

import com.cashito.domain.repositories.auth.AuthRepository

/**
 * Caso de uso para cerrar la sesión del usuario actual.
 */
class LogoutUseCase(private val authRepository: AuthRepository) {

    /**
     * Ejecuta el caso de uso para el logout.
     *
     * @return Un [Result] que indica si la operación fue exitosa o no.
     */
    suspend operator fun invoke(): Result<Unit> {
        return try {
            authRepository.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
