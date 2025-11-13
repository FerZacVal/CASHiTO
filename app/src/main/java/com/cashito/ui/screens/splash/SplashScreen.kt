package com.cashito.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.R
import com.cashito.Routes
import com.cashito.domain.usecases.auth.AutoLoginResult
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.SplashViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = koinViewModel()
) {
    val autoLoginResult by viewModel.autoLoginResult.collectAsState()

    LaunchedEffect(autoLoginResult) {
        when (autoLoginResult) {
            is AutoLoginResult.Success -> {
                // Navegar al Dashboard y limpiar la pila para que no se pueda volver atrás
                navController.navigate(Routes.DASHBOARD) { 
                    popUpTo(Routes.SPLASH) { inclusive = true } 
                }
            }
            is AutoLoginResult.Failure, is AutoLoginResult.NoCredentials -> {
                // Navegar al Login y limpiar la pila
                navController.navigate(Routes.LOGIN) { 
                    popUpTo(Routes.SPLASH) { inclusive = true } 
                }
            }
            null -> { 
                // El ViewModel está trabajando, no hacemos nada y mostramos la UI de carga.
            }
        }
    }
    
    // La UI ahora solo muestra el logo y un indicador de carga.
    SplashScreenContent()
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceContainerLow,
                        MaterialTheme.colorScheme.background
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
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💰",
                    style = MaterialTheme.typography.displayLarge
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            Text(
                text = stringResource(id = R.string.splash_title),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = stringResource(id = R.string.splash_subtitle),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.weight(1f))

            // Mostramos un indicador de progreso en lugar de botones
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    CASHiTOTheme {
        SplashScreenContent()
    }
}
