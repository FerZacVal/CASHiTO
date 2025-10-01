package com.cashito.ui.screens.goal_form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.theme.Background
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.PrimaryGreen
import com.cashito.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalFormScreen(
    navController: NavController,
    isEditing: Boolean = false,
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onSaveGoal: (GoalFormData) -> Unit = { /* Handle save */ navController.popBackStack() }
) {
    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedIcon by remember { mutableStateOf("💻") }
    var selectedColor by remember { mutableStateOf(PrimaryGreen) }
    var isRecurringEnabled by remember { mutableStateOf(false) }
    var recurringAmount by remember { mutableStateOf("") }
    var recurringFrequency by remember { mutableStateOf("Semanal") }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (isEditing) "Editar meta" else "Crear meta",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Semibold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg)
        ) {
            // Goal Name
            CashitoTextField(
                value = goalName,
                onValueChange = { goalName = it },
                label = "Nombre de la meta",
                placeholder = "Viaje a Cusco",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            // Target Amount
            CashitoTextField(
                value = targetAmount,
                onValueChange = { targetAmount = it },
                label = "Monto objetivo",
                placeholder = "S/ 4,000",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            // Target Date
            Text(
                text = "Fecha objetivo",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Card(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Text(
                        text = if (selectedDate != null) {
                            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            formatter.format(Date(selectedDate!!))
                        } else {
                            "Seleccionar fecha"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedDate != null) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            // Icon Selection
            Text(
                text = "Icono de la meta",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(getGoalIcons()) { icon ->
                    IconSelectionButton(
                        icon = icon.icon,
                        isSelected = selectedIcon == icon.icon,
                        onClick = { selectedIcon = icon.icon }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.lg))
            
            // Color Selection
            Text(
                text = "Color de la meta",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(getGoalColors()) { color ->
                    ColorSelectionButton(
                        color = color,
                        isSelected = selectedColor == color,
                        onClick = { selectedColor = color }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            // Recurring Savings
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Aporte recurrente",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = isRecurringEnabled,
                            onCheckedChange = { isRecurringEnabled = it },
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryGreen,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.Gray
                            )
                        )
                    }
                    
                    if (isRecurringEnabled) {
                        Spacer(modifier = Modifier.height(Spacing.lg))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            CashitoTextField(
                                value = recurringAmount,
                                onValueChange = { recurringAmount = it },
                                label = "Monto",
                                placeholder = "S/ 200",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                            
                            CashitoTextField(
                                value = recurringFrequency,
                                onValueChange = { recurringFrequency = it },
                                label = "Frecuencia",
                                placeholder = "Semanal",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        
                        Text(
                            text = "Sugerencia: programa aportes automáticos para avanzar sin pensar.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            // Save Button
            PrimaryButton(
                text = if (isEditing) "Guardar cambios" else "Crear meta",
                onClick = {
                    if (goalName.isNotEmpty() && targetAmount.isNotEmpty() && selectedDate != null) {
                        val goalData = GoalFormData(
                            name = goalName,
                            targetAmount = targetAmount,
                            targetDate = selectedDate!!,
                            icon = selectedIcon,
                            color = selectedColor,
                            isRecurring = isRecurringEnabled,
                            recurringAmount = if (isRecurringEnabled) recurringAmount else "",
                            recurringFrequency = if (isRecurringEnabled) recurringFrequency else ""
                        )
                        onSaveGoal(goalData)
                    }
                },
                enabled = goalName.isNotEmpty() && targetAmount.isNotEmpty() && selectedDate != null,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDate = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun IconSelectionButton(
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryGreen else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        shape = RoundedCornerShape(Spacing.md)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
fun ColorSelectionButton(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        shape = CircleShape
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Data classes
data class GoalFormData(
    val name: String,
    val targetAmount: String,
    val targetDate: Long,
    val icon: String,
    val color: Color,
    val isRecurring: Boolean,
    val recurringAmount: String,
    val recurringFrequency: String
)

data class GoalIcon(
    val icon: String
)

// Sample data
fun getGoalIcons(): List<GoalIcon> = listOf(
    GoalIcon("💻"),
    GoalIcon("✈️"),
    GoalIcon("🏠"),
    GoalIcon("🚗"),
    GoalIcon("🎓"),
    GoalIcon("💍"),
    GoalIcon("🏖️"),
    GoalIcon("📱")
)

fun getGoalColors(): List<Color> = listOf(
    PrimaryGreen,
    Color(0xFF3B82F6),
    Color(0xFF8B5CF6),
    Color(0xFFF59E0B),
    Color(0xFFEF4444),
    Color(0xFF10B981),
    Color(0xFFEC4899),
    Color(0xFF6366F1)
)
