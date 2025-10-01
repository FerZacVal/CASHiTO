package com.cashito.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.theme.Background
import com.cashito.ui.theme.PrimaryGreen
import com.cashito.ui.theme.Spacing
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    onNavigateToLogin: () -> Unit = { navController.navigate("login") }
) {
    // Auto-navigate after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Background,
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = PrimaryGreen,
                        shape = androidx.compose.foundation.shape.Circle
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💰",
                    style = MaterialTheme.typography.displayLarge
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            // App Name
            Text(
                text = "Cashito",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Tagline
            Text(
                text = "Ahorra fácil. Llega a tus metas.",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(Spacing.xxxl))
            
            // CTA Button
            PrimaryButton(
                text = "Comenzar",
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            // Login Link
            Text(
                text = "¿Ya tienes cuenta? Iniciar sesión",
                style = MaterialTheme.typography.bodyLarge,
                color = PrimaryGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
