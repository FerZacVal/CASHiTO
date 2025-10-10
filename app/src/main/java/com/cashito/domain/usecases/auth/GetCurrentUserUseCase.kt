package com.cashito.domain.usecases.auth

import com.cashito.domain.entities.auth.User
import com.cashito.domain.repositories.auth.AuthRepository

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}
