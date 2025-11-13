package com.cashito.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.core.BiometricAuthenticator
import com.cashito.core.BiometricAuthStatus
import com.cashito.domain.repositories.auth.AuthRepository // CAMBIO: Usar AuthRepository
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
    private val authRepository: AuthRepository // CAMBIO: Inyectar AuthRepository
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
        viewModelScope.launch { // Es una función suspend
            val isAvailable = BiometricAuthenticator.isBiometricAuthAvailable(context) && authRepository.hasSavedCredentials()
            _uiState.value = _uiState.value.copy(isBiometricAuthAvailable = isAvailable)
        }
    }

    private fun loginWithSavedCredentials() {
        viewModelScope.launch {
            authRepository.getSavedCredentials()?.let { (email, password) ->
                loginUseCase(email, password)
                    .onSuccess { _uiState.value = _uiState.value.copy(isLoginSuccess = true) }
                    .onFailure { 
                        authRepository.clearCredentials() // Limpiar credenciales si fallan
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
        // Aquí iría la validación de campos, que no cambia.

        if (state.emailError == null && state.passwordError == null && state.email.isNotBlank() && state.password.isNotBlank()) {
            viewModelScope.launch {
                loginUseCase(state.email, state.password)
                    .onSuccess {
                        if (state.rememberMe) {
                            // Guardar si la casilla está marcada
                            authRepository.saveCredentials(state.email, state.password)
                        } else {
                            // Borrar si la casilla no está marcada
                            authRepository.clearCredentials()
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
