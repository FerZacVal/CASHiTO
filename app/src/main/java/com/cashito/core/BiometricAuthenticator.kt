package com.cashito.core

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- Result State ---
enum class BiometricAuthStatus {
    SUCCESS,
    FAILED,
    ERROR,
    IDLE
}

object BiometricAuthenticator {

    private var activity: FragmentActivity? = null

    private val _authStatus = MutableStateFlow(BiometricAuthStatus.IDLE)
    val authStatus = _authStatus.asStateFlow()

    fun registerActivity(activity: FragmentActivity) {
        this.activity = activity
    }

    fun unregisterActivity() {
        this.activity = null
    }

    fun isBiometricAuthAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    fun showBiometricPrompt() {
        val activity = this.activity ?: return // Don't do anything if no activity is registered

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        _authStatus.value = BiometricAuthStatus.ERROR
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    _authStatus.value = BiometricAuthStatus.FAILED
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    _authStatus.value = BiometricAuthStatus.SUCCESS
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación biométrica")
            .setSubtitle("Inicia sesión usando tu huella dactilar")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
    
    fun resetStatus() {
        _authStatus.value = BiometricAuthStatus.IDLE
    }
}