package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.usecases.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- STATE ---
data class CreateUserUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val nombreError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val registrationError: String? = null, // For general registration errors
    val isRegistrationSuccess: Boolean = false
)

// --- VIEWMODEL ---
class CreateUserViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUserUiState())
    val uiState: StateFlow<CreateUserUiState> = _uiState.asStateFlow()

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, nombreError = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onRegisterClick() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, registrationError = null) }
            
            val state = _uiState.value
            val result = registerUseCase(state.email, state.password, state.nombre)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isRegistrationSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, registrationError = e.message ?: "Error desconocido") }
                }
            )
        }
    }

    private fun validateInputs(): Boolean {
        val state = _uiState.value
        val nombreError = if (state.nombre.isBlank()) "El nombre es requerido" else null
        val emailError = when {
            state.email.isBlank() -> "El correo es requerido"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> "Formato de correo inválido"
            else -> null
        }
        val passwordError = when {
            state.password.isBlank() -> "La contraseña es requerida"
            state.password.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }

        _uiState.update {
            it.copy(
                nombreError = nombreError,
                emailError = emailError,
                passwordError = passwordError
            )
        }

        return nombreError == null && emailError == null && passwordError == null
    }
}
