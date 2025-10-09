package com.cashito.domain.usecases.auth

import com.cashito.domain.entities.auth.User
import com.cashito.domain.repositories.auth.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener el estado de autenticación actual.
 *
 * Expone un Flow que emite el [User] si hay una sesión activa, o null si no la hay.
 * Es ideal para que la capa de UI observe los cambios de estado en tiempo real y
 * decida a qué pantalla navegar (ej. Login vs. Dashboard).
 */
class GetAuthStateUseCase(private val authRepository: AuthRepository) {

    /**
     * Ejecuta el caso de uso.
     *
     * @return Un [Flow] que emite el [User] actual o null.
     */
    operator fun invoke(): Flow<User?> {
        return authRepository.getAuthState()
    }
}
