package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        // TODO: Replace with actual data fetching from a repository
        _uiState.value = ProfileUiState(
            userName = "Ana García",
            userEmail = "ana.garcia@email.com",
            userInitial = "A",
            isLoading = false
        )
    }

    fun onBiometricEnabledChange(enabled: Boolean) {
        // TODO: Save preference
        _uiState.value = _uiState.value.copy(biometricEnabled = enabled)
    }

    fun onNotificationsEnabledChange(enabled: Boolean) {
        // TODO: Save preference
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
    }

    fun onRemindersEnabledChange(enabled: Boolean) {
        // TODO: Save preference
        _uiState.value = _uiState.value.copy(remindersEnabled = enabled)
    }

    fun onLogoutClick() {
        // TODO: Clear session, etc.
        _uiState.value = _uiState.value.copy(loggedOut = true)
    }
}
