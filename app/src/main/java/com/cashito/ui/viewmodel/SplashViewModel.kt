package com.cashito.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.usecases.auth.AutoLoginResult
import com.cashito.domain.usecases.auth.AutoLoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Splash.
 *
 * Su única responsabilidad es ejecutar el caso de uso de auto-login y comunicar
 * el resultado a la UI para que esta decida a qué pantalla navegar.
 */
class SplashViewModel(private val autoLoginUseCase: AutoLoginUseCase) : ViewModel() {

    private val _autoLoginResult = MutableStateFlow<AutoLoginResult?>(null)
    val autoLoginResult: StateFlow<AutoLoginResult?> = _autoLoginResult.asStateFlow()

    init {
        // Iniciar el proceso de auto-login tan pronto como se cree el ViewModel
        viewModelScope.launch {
            _autoLoginResult.value = autoLoginUseCase()
        }
    }
}
