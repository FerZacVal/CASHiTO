package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.usecases.auth.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- STATE ---
data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val userInitial: String = "",
    val biometricEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val remindersEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val loggedOut: Boolean = false
)

// --- VIEWMODEL ---
class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = getCurrentUserUseCase()
            if (user != null) {
                _uiState.update {
                    it.copy(
                        userName = user.displayName ?: "",
                        userEmail = user.email ?: "",
                        userInitial = user.displayName?.firstOrNull()?.toString() ?: "",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) } // Handle user not found
            }
        }
    }

    fun onBiometricEnabledChange(enabled: Boolean) {
        // TODO: Save preference
        _uiState.update { it.copy(biometricEnabled = enabled) }
    }

    fun onNotificationsEnabledChange(enabled: Boolean) {
        // TODO: Save preference
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun onRemindersEnabledChange(enabled: Boolean) {
        // TODO: Save preference
        _uiState.update { it.copy(remindersEnabled = enabled) }
    }

    fun onLogoutClick() {
        // TODO: Clear session, etc.
        _uiState.update { it.copy(loggedOut = true) }
    }
}
