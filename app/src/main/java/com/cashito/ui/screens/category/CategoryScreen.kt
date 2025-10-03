package com.cashito.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Mantener para sampleCategories si los colores son fijos
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.Routes

// Considera mover estas definiciones de color a tu archivo Color.kt si son estáticas
// o encontrar una estrategia para que sean compatibles con el tema si es necesario.
// Por ahora, las mantendremos aquí para el ejemplo.
val categoryFoodColor = Color(0xFF4CAF50)
val categoryTransportationColor = Color(0xFF2196F3)
val categoryShoppingColor = Color(0xFF9C27B0)
val categoryEntertainmentColor = Color(0xFFFF9800)
val categoryHealthcareColor = Color(0xFFF44336)
val categoryUtilitiesColor = Color(0xFF607D8B)

@Composable
fun CategoryScreen(navController: NavController? = null) {
    var newCategoryName by remember { mutableStateOf("") }

    // Si estos colores fueran parte del tema, los referenciarías
    // desde MaterialTheme.colorScheme o una extensión personalizada.
    // Como son colores específicos para datos, está bien definirlos así
    // o moverlos a Color.kt para mejor organización.
    val sampleCategories = remember { // Usar remember para que la lista no se recree en cada recomposición
        listOf(
            "Food & Dining" to categoryFoodColor,
            "Transportation" to categoryTransportationColor,
            "Shopping" to categoryShoppingColor,
            "Entertainment" to categoryEntertainmentColor,
            "Healthcare" to categoryHealthcareColor,
            "Utilities" to categoryUtilitiesColor
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                // Utiliza headlineMedium de AppTypography (Type.kt)
                style = MaterialTheme.typography.headlineMedium,
                // Considera mover FontWeight.Bold a la definición de headlineMedium en Type.kt
                // si todos los headlineMedium deben ser Bold.
                fontWeight = FontWeight.Bold
            )

            FloatingActionButton(
                onClick = { /* TODO: Add category */ },
                modifier = Modifier.size(48.dp),
                // Los colores del FAB se toman de MaterialTheme.colorScheme
                // (por ejemplo, containerColor = primaryContainer o secondaryContainer,
                // contentColor = onPrimaryContainer u onSecondaryContainer, según el tema)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            // El containerColor se toma de primaryContainer del tema, como se especifica.
            // Los demás colores (como el del contenido) se derivarán de él.
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Add New Category",
                    // Utiliza titleMedium de AppTypography (Type.kt)
                    style = MaterialTheme.typography.titleMedium,
                    // Considera mover FontWeight.Medium a la definición de titleMedium en Type.kt
                    // si todos los titleMedium deben tener este peso.
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category Name") }, // La tipografía y color de la etiqueta vienen del tema
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) }, // El tinte del icono viene del tema
                    modifier = Modifier.fillMaxWidth()
                    // Colores de borde, texto, etc., se toman de MaterialTheme.colorScheme
                    // La tipografía del texto ingresado también viene de MaterialTheme.typography
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { /* TODO: Save category */ },
                    modifier = Modifier.fillMaxWidth()
                    // Colores de fondo y texto se toman de MaterialTheme.colorScheme
                ) {
                    // La tipografía de este texto viene de MaterialTheme.typography (usualmente button)
                    Text("Add Category")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Categories",
            // Utiliza titleMedium de AppTypography (Type.kt)
            style = MaterialTheme.typography.titleMedium,
            // Considera mover FontWeight.Medium a la definición de titleMedium en Type.kt
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sampleCategories) { (name, color) ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                    // Los colores del Card se toman por defecto del tema (surface, onSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // El Box original para el indicador de color no tenía un modificador .background()
                        // por lo que no mostraría el color.
                        // El padding también era un poco confuso. Simplificado:
                        Box(
                            modifier = Modifier
                                .size(16.dp) // Tamaño del indicador
                                .background(color) // Aplicar el color de la categoría
                        )

                        Spacer(modifier = Modifier.width(12.dp)) // Espacio entre el indicador y el texto

                        Text(
                            text = name,
                            // Utiliza bodyLarge de AppTypography (Type.kt)
                            style = MaterialTheme.typography.bodyLarge
                            // El color del texto se tomará de MaterialTheme.colorScheme.onSurface (o similar)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController?.navigate(Routes.HOME) },
            modifier = Modifier.fillMaxWidth()
            // Los colores de borde y texto se toman de MaterialTheme.colorScheme
        ) {
            // La tipografía de este texto viene de MaterialTheme.typography (usualmente button)
            Text("Back to Home")
        }
    }
}
