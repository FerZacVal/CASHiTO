package com.cashito.domain.usecases.auth

import com.cashito.domain.entities.auth.User
import com.cashito.domain.repositories.auth.AuthException
import com.cashito.domain.repositories.auth.AuthRepository

/**
 * Caso de uso para registrar un nuevo usuario.
 */
class RegisterUseCase(private val authRepository: AuthRepository) {

    /**
     * Ejecuta el caso de uso para el registro.
     *
     * @param email El email del nuevo usuario.
     * @param password La contraseña del nuevo usuario.
     * @return Un [Result] que encapsula el [User] en caso de éxito o la [AuthException] en caso de fallo.
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return try {
            // Aquí podría ir lógica de negocio adicional si fuera necesario,
            // como validar la fortaleza de la contraseña a nivel de dominio.
            val user = authRepository.register(email, password)
            Result.success(user)
        } catch (e: AuthException) {
            Result.failure(e)
        } catch (e: Exception) {
            // Captura de cualquier otro error inesperado
            Result.failure(e)
        }
    }
}
