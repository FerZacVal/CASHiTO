package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- STATE ---
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoginSuccess: Boolean = false
)

// --- VIEWMODEL ---
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = null)
    }

    fun onRememberMeChange(rememberMe: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = rememberMe)
    }

    fun onLoginClick() {
        val state = _uiState.value

        // Validations
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
            emailError = emailError,
            passwordError = passwordError
        )

        if (emailError == null && passwordError == null) {
            // TODO: Implement actual login logic
            _uiState.value = _uiState.value.copy(isLoginSuccess = true)
        }
    }
}
