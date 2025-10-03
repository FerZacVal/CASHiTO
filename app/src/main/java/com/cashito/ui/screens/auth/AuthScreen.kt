package com.cashito.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.Routes

@Composable
fun AuthScreen(navController: NavController? = null) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), // Espaciado general, no directamente del tema de color/tipo
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome Back",
            // Utiliza el estilo 'headlineLarge' definido en tu AppTypography (Type.kt)
            // a través de MaterialTheme. Si personalizaste AppTypography, esos cambios se reflejarán aquí.
            style = MaterialTheme.typography.headlineLarge,
            // fontWeight es una personalización local.
            // Si quieres que todos los headlineLarge sean Bold, defínelo en Type.kt.
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Sign in to your account",
            // Utiliza el estilo 'bodyLarge' definido en tu AppTypography (Type.kt)
            style = MaterialTheme.typography.bodyLarge,
            // Utiliza el color 'onSurfaceVariant' del ColorScheme actual (lightScheme o darkScheme)
            // definido en Theme.kt y basado en Color.kt.
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
            // Los colores del OutlinedTextField (borde, texto, etiqueta, icono)
            // se toman automáticamente del MaterialTheme.colorScheme.
            // La tipografía del texto ingresado y la etiqueta también vienen de MaterialTheme.typography.
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
            // Al igual que el campo de email, los colores y la tipografía
            // se derivan del MaterialTheme.
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* TODO: Implement login */ },
            modifier = Modifier.fillMaxWidth()
            // El color de fondo del botón (normalmente primary) y el color del texto (normalmente onPrimary)
            // provienen de MaterialTheme.colorScheme.
        ) {
            // El estilo de este texto (por ejemplo, button) se toma de MaterialTheme.typography.
            Text("Sign In", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { /* TODO: Implement sign up */ }
            // El color del texto (normalmente primary) proviene de MaterialTheme.colorScheme.
        ) {
            // El estilo de este texto (por ejemplo, button) se toma de MaterialTheme.typography.
            Text("Don't have an account? Sign Up")
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { navController?.navigate(Routes.HOME) },
            modifier = Modifier.fillMaxWidth()
            // El color del borde y el color del texto (normalmente primary)
            // provienen de MaterialTheme.colorScheme.
        ) {
            // El estilo de este texto (por ejemplo, button) se toma de MaterialTheme.typography.
            Text("Back to Home")
        }
    }
}
