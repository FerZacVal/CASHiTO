package com.cashito.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.core.BiometricAuthenticator
import com.cashito.core.BiometricAuthStatus
import com.cashito.core.CredentialsManager
import com.cashito.domain.usecases.auth.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

// --- STATE ---
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoginSuccess: Boolean = false,
    val isBiometricAuthAvailable: Boolean = false,
    val showBiometricPrompt: Boolean = false
)

// --- VIEWMODEL ---
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val credentialsManager: CredentialsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        observeBiometricAuthStatus()
    }

    private fun observeBiometricAuthStatus() {
        BiometricAuthenticator.authStatus
            .onEach { status ->
                if (status == BiometricAuthStatus.SUCCESS) {
                    BiometricAuthenticator.resetStatus()
                    loginWithSavedCredentials()
                }
            }
            .launchIn(viewModelScope)
    }

    fun checkBiometricAuth(context: Context) {
        val isAvailable = BiometricAuthenticator.isBiometricAuthAvailable(context) && credentialsManager.hasCredentials()
        _uiState.value = _uiState.value.copy(isBiometricAuthAvailable = isAvailable)
    }

    private fun loginWithSavedCredentials() {
        credentialsManager.getCredentials()?.let { (email, password) ->
            viewModelScope.launch {
                loginUseCase(email, password)
                    .onSuccess { _uiState.value = _uiState.value.copy(isLoginSuccess = true) }
                    .onFailure { 
                        // If stored credentials fail, clear them.
                        credentialsManager.clearCredentials()
                        _uiState.value = _uiState.value.copy(passwordError = "Tus credenciales guardadas han caducado. Por favor, inicia sesión de nuevo.") 
                    }
            }
        }
    }

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
            viewModelScope.launch {
                loginUseCase(state.email, state.password)
                    .onSuccess {
                        if (state.rememberMe) {
                            credentialsManager.saveCredentials(state.email, state.password)
                        }
                        _uiState.value = _uiState.value.copy(isLoginSuccess = true)
                    }
                    .onFailure { 
                        _uiState.value = _uiState.value.copy(passwordError = it.message)
                    }
            }
        }
    }

    fun onBiometricLoginClick() {
        BiometricAuthenticator.showBiometricPrompt()
    }
}