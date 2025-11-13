package com.cashito.domain.usecases.auth

import com.cashito.domain.repositories.auth.AuthRepository

/**
 * Resultado del intento de inicio de sesión automático.
 */
sealed class AutoLoginResult {
    /**
     * El inicio de sesión automático fue exitoso.
     */
    data object Success : AutoLoginResult()

    /**
     * Falló el inicio de sesión (ej. credenciales expiradas o inválidas).
     */
    data object Failure : AutoLoginResult()

    /**
     * No hay credenciales guardadas para intentar el inicio de sesión.
     */
    data object NoCredentials : AutoLoginResult()
}

/**
 * Caso de uso que intenta iniciar sesión automáticamente utilizando las credenciales
 * guardadas en el repositorio.
 */
class AutoLoginUseCase(
    private val authRepository: AuthRepository,
    private val loginUseCase: LoginUseCase
) {

    /**
     * Ejecuta el caso de uso.
     *
     * @return [AutoLoginResult] indicando el resultado del proceso.
     */
    suspend operator fun invoke(): AutoLoginResult {
        // 1. Intentar obtener las credenciales guardadas.
        val credentials = authRepository.getSavedCredentials()
            ?: return AutoLoginResult.NoCredentials

        // 2. Si existen, intentar iniciar sesión con ellas.
        val (email, password) = credentials
        val loginResult = loginUseCase(email, password)

        return if (loginResult.isSuccess) {
            // 3. Si el login es exitoso, devolver Success.
            AutoLoginResult.Success
        } else {
            // 4. Si falla (credenciales inválidas, etc.), limpiar las credenciales corruptas y devolver Failure.
            authRepository.clearCredentials()
            AutoLoginResult.Failure
        }
    }
}
