package com.cashito.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.R
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SecondaryButton
import com.cashito.ui.components.buttons.SmallButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.LoginUiState
import com.cashito.ui.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Check for biometric availability when the screen is first composed
    LaunchedEffect(Unit) {
        viewModel.checkBiometricAuth(context)
    }

    val onNavigateToDashboard: () -> Unit = { navController.navigate("dashboard") }

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onNavigateToDashboard()
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRememberMeChange = viewModel::onRememberMeChange,
        onLoginClick = viewModel::onLoginClick,
        onBiometricLoginClick = viewModel::onBiometricLoginClick, // Simplified call
        onNavigateToRegister = { navController.navigate("register") },
        onForgotPasswordClick = { /* TODO */ }
    )
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onBiometricLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.xxxl))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💰",
                    style = MaterialTheme.typography.displayLarge,
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = stringResource(id = R.string.login_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.xxxl))

            CashitoTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = stringResource(id = R.string.login_email_label),
                placeholder = stringResource(id = R.string.login_email_placeholder),
                keyboardType = KeyboardType.Email,
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            CashitoTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = stringResource(id = R.string.login_password_label),
                placeholder = stringResource(id = R.string.login_password_placeholder),
                isPassword = true,
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.rememberMe,
                    onCheckedChange = onRememberMeChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Text(
                    text = stringResource(id = R.string.login_remember_me_checkbox),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            PrimaryButton(
                text = stringResource(id = R.string.login_login_button),
                onClick = onLoginClick
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            SecondaryButton(
                text = stringResource(id = R.string.login_create_account_button),
                onClick = onNavigateToRegister
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            Text(
                text = stringResource(id = R.string.login_or_continue_with),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                SmallButton(
                    text = stringResource(id = R.string.login_google_button),
                    onClick = { /* Handle Google login */ },
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )

                if (uiState.isBiometricAuthAvailable) {
                    SmallButton(
                        text = stringResource(id = R.string.login_fingerprint_button),
                        onClick = onBiometricLoginClick,
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            TextButton(
                onClick = onForgotPasswordClick
            ) {
                Text(
                    text = stringResource(id = R.string.login_forgot_password_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    CASHiTOTheme {
        LoginScreenContent(
            uiState = LoginUiState(email = "test@cashito.com", passwordError = "Mínimo 6 caracteres", isBiometricAuthAvailable = true),
            onEmailChange = {},
            onPasswordChange = {},
            onRememberMeChange = {},
            onLoginClick = {},
            onBiometricLoginClick = {},
            onNavigateToRegister = {},
            onForgotPasswordClick = {}
        )
    }
}
