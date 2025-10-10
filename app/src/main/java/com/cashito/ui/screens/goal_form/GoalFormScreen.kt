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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.PrimaryButton
import com.cashito.ui.components.inputs.CashitoTextField
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.GoalFormUiState
import com.cashito.ui.viewmodel.GoalFormViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalFormScreen(
    navController: NavController,
    viewModel: GoalFormViewModel = viewModel(),
    isEditing: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(uiState.goalSaved) {
        if (uiState.goalSaved) {
            navController.popBackStack()
        }
    }

    GoalFormScreenContent(
        uiState = uiState,
        datePickerState = datePickerState,
        isEditing = isEditing,
        onGoalNameChange = viewModel::onGoalNameChange,
        onTargetAmountChange = viewModel::onTargetAmountChange,
        onDatePickerDismiss = viewModel::onDatePickerDismiss,
        onDateSelected = { date ->
            viewModel.onDateSelected(date)
            viewModel.onDatePickerDismiss(false)
        },
        onIconSelected = viewModel::onIconSelected,
        onColorSelected = viewModel::onColorSelected,
        onSaveGoal = viewModel::onSaveGoal,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalFormScreenContent(
    uiState: GoalFormUiState,
    datePickerState: DatePickerState,
    isEditing: Boolean,
    onGoalNameChange: (String) -> Unit,
    onTargetAmountChange: (String) -> Unit,
    onDatePickerDismiss: (Boolean) -> Unit,
    onDateSelected: (Long?) -> Unit,
    onIconSelected: (String) -> Unit,
    onColorSelected: (Color) -> Unit,
    onSaveGoal: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val goalIcons = getGoalIcons()
    val goalColors = getGoalColors()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar meta" else "Crear meta", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { paddingValuesScaffold ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValuesScaffold)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.lg)
        ) {
            CashitoTextField(
                value = uiState.goalName,
                onValueChange = onGoalNameChange,
                label = "Nombre de la meta",
                placeholder = "Viaje a Cusco",
                isError = uiState.goalNameError != null,
                errorMessage = uiState.goalNameError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            CashitoTextField(
                value = uiState.targetAmount,
                onValueChange = onTargetAmountChange,
                label = "Monto objetivo",
                placeholder = "S/ 4,000",
                keyboardType = KeyboardType.Number,
                isError = uiState.targetAmountError != null,
                errorMessage = uiState.targetAmountError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text("Fecha objetivo", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(Spacing.sm))

            Card(
                onClick = { onDatePickerDismiss(true) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DateRange, "Date", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Text(
                        text = uiState.selectedDate?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) } ?: "Seleccionar fecha",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (uiState.selectedDate != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (uiState.showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { onDatePickerDismiss(false) },
                    confirmButton = { TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) { Text("OK") } },
                    dismissButton = { TextButton(onClick = { onDatePickerDismiss(false) }) { Text("Cancelar") } }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text("Icono de la meta", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(Spacing.sm))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                items(goalIcons) { icon ->
                    IconSelectionButton(icon = icon, isSelected = uiState.selectedIcon == icon, onClick = { onIconSelected(icon) })
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text("Color de la meta", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(Spacing.sm))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                items(goalColors) { color ->
                    ColorSelectionButton(color = color, isSelected = uiState.selectedColor == color, onClick = { onColorSelected(color) })
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))

            PrimaryButton(text = if (isEditing) "Guardar cambios" else "Crear meta", onClick = onSaveGoal, enabled = uiState.isFormValid)
        }
    }
}

@Composable
fun IconSelectionButton(icon: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(ComponentSize.iconSize * 2)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = icon, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun ColorSelectionButton(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(ComponentSize.iconSize * 2)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier.size(ComponentSize.iconSize).clip(CircleShape).background(Color.White.copy(alpha = 0.5f))
            )
        }
    }
}

fun getGoalIcons(): List<String> = listOf("✈️", "💻", "🛡️", "🎁", "🏠", "🚗", "🎓", "💍")

@Composable
fun getGoalColors(): List<Color> = listOf(
    MaterialTheme.colorScheme.primary,
    MaterialTheme.colorScheme.secondary,
    MaterialTheme.colorScheme.tertiary,
    Color(0xFFF59E0B),
    Color(0xFFEC4899),
    Color(0xFF8B5CF6)
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GoalFormScreenPreview() {
    CASHiTOTheme {
        GoalFormScreenContent(
            uiState = GoalFormUiState(goalNameError = "El nombre es requerido", isFormValid = true, selectedDate = System.currentTimeMillis()),
            datePickerState = rememberDatePickerState(),
            isEditing = false,
            onGoalNameChange = {},
            onTargetAmountChange = {},
            onDatePickerDismiss = {},
            onDateSelected = {},
            onIconSelected = {},
            onColorSelected = {},
            onSaveGoal = {},
            onNavigateBack = {}
        )
    }
}
