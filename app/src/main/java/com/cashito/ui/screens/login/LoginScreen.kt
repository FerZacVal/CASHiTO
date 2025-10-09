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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SecondaryButton
import com.cashito.ui.components.buttons.SmallButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Spacing

@Composable
fun LoginScreen(
    navController: NavController,
    onNavigateToDashboard: () -> Unit = { navController.navigate("dashboard") },
    onNavigateToRegister: () -> Unit = { navController.navigate("register") }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

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
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.xxxl))

            CashitoTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                label = "Correo electrónico",
                placeholder = "correo@ejemplo.com",
                keyboardType = KeyboardType.Email,
                isError = emailError.isNotEmpty(),
                errorMessage = emailError
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            CashitoTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ""
                },
                label = "Contraseña",
                placeholder = "Contraseña",
                isPassword = true,
                isError = passwordError.isNotEmpty(),
                errorMessage = passwordError
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Text(
                    text = "Mantener sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            PrimaryButton(
                text = "Iniciar sesión",
                onClick = {
                    if (email.isEmpty()) {
                        emailError = "El correo es requerido"
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Formato de correo inválido"
                    }

                    if (password.isEmpty()) {
                        passwordError = "La contraseña es requerida"
                    } else if (password.length < 6) {
                        passwordError = "Mínimo 6 caracteres"
                    }

                    if (emailError.isEmpty() && passwordError.isEmpty()) {
                        onNavigateToDashboard()
                    }
                }
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            SecondaryButton(
                text = "Crear cuenta",
                onClick = onNavigateToRegister
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            Text(
                text = "O continúa con",
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
                    text = "Google",
                    onClick = { /* Handle Google login */ },
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )

                SmallButton(
                    text = "Apple",
                    onClick = { /* Handle Apple login */ },
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            TextButton(
                onClick = { /* Handle forgot password */ }
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
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
        LoginScreen(
            navController = rememberNavController(),
            onNavigateToDashboard = {},
            onNavigateToRegister = {}
        )
    }
}
