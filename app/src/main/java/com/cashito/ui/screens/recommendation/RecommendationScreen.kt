package com.cashito.ui.screens.recommendation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.Routes
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.buttons.SecondaryButton
import com.cashito.ui.theme.Spacing

@Composable
fun RecommendationScreen(navController: NavController? = null) {
    val sampleRecommendations = listOf(
        "💡 Considera cocinar en casa más a menudo para reducir los gastos de comida",
        "📊 Tus costos de transporte son un 15% más altos que el promedio",
        "💰 Podrías ahorrar S/ 50 al mes cambiando a un plan de telefonía diferente",
        "🎯 Configura transferencias automáticas a tu cuenta de ahorros",
        "📈 Tu presupuesto de entretenimiento ha aumentado un 30% este mes"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recomendaciones",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { /* TODO: Refresh recommendations */ }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.width(Spacing.md))
                Text(
                    text = "Ideas basadas en IA para ayudarte a ahorrar dinero",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = "Consejos Personalizados",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            items(sampleRecommendations) { recommendation ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "Generar Nuevas Recomendaciones",
            onClick = { /* TODO: Generate new recommendations */ }
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        SecondaryButton(
            text = "Volver al Inicio",
            onClick = { navController?.navigate(Routes.HOME) }
        )
    }
}