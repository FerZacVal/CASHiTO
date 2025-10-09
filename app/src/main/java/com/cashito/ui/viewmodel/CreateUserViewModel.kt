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
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val nameError: String? = null,
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

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
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
            try {
                val state = _uiState.value
                registerUseCase(state.email, state.password)
                _uiState.update { it.copy(isLoading = false, isRegistrationSuccess = true) }
            } catch (e: Exception) {
                // Here you could map specific exceptions to user-friendly messages
                _uiState.update { it.copy(isLoading = false, registrationError = e.message ?: "Error desconocido") }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val state = _uiState.value
        val nameError = if (state.name.isBlank()) "El nombre es requerido" else null
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
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError
            )
        }

        return nameError == null && emailError == null && passwordError == null
    }
}
