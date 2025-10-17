package com.cashito.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.cashito.R
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.viewmodel.CreateUserViewModel
import com.cashito.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateUserScreen(
    navController: NavController,
    viewModel: CreateUserViewModel = koinViewModel(),
    onNavigateToLogin: () -> Unit = { navController.navigate("login") }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isRegistrationSuccess) {
        if (uiState.isRegistrationSuccess) {
            onNavigateToLogin()
        }
    }

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.create_user_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.xxxl))

            CashitoTextField(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = stringResource(id = R.string.create_user_name_label),
                placeholder = stringResource(id = R.string.create_user_name_placeholder),
                isError = uiState.nombreError != null,
                errorMessage = uiState.nombreError
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            CashitoTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = stringResource(id = R.string.create_user_email_label),
                placeholder = stringResource(id = R.string.create_user_email_placeholder),
                keyboardType = KeyboardType.Email,
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            CashitoTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = stringResource(id = R.string.create_user_password_label),
                placeholder = stringResource(id = R.string.create_user_password_placeholder),
                isPassword = true,
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            PrimaryButton(
                text = stringResource(id = R.string.create_user_create_account_button),
                onClick = viewModel::onRegisterClick
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            TextButton(
                onClick = onNavigateToLogin
            ) {
                Text(
                    text = stringResource(id = R.string.create_user_login_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
