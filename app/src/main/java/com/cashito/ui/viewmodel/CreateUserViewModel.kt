package com.cashito.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- STATE ---
data class CreateUserUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isRegistrationSuccess: Boolean = false
)

// --- VIEWMODEL ---
class CreateUserViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUserUiState())
    val uiState: StateFlow<CreateUserUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, nameError = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null)
    }

    fun onRegisterClick() {
        val state = _uiState.value
        var hasError = false

        // Validations
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

        _uiState.value = state.copy(
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError
        )

        if (nameError == null && emailError == null && passwordError == null) {
            // TODO: Implement user creation logic with a repository or use case
            // For now, we simulate a successful registration
            _uiState.value = _uiState.value.copy(isRegistrationSuccess = true)
        }
    }
}
