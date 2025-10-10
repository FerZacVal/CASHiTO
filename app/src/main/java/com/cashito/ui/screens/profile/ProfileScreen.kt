package com.cashito.ui.screens.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.SecondaryButton
import com.cashito.ui.components.list.CashitoListItem
import com.cashito.ui.theme.CASHiTOTheme
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.ProfileUiState
import com.cashito.ui.viewmodel.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val onNavigateToLogin: () -> Unit = { navController.navigate("login") { popUpTo(navController.graph.id) { inclusive = true } } }

    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) {
            onNavigateToLogin()
        }
    }

    ProfileScreenContent(
        uiState = uiState,
        onBiometricChange = viewModel::onBiometricEnabledChange,
        onNotificationsChange = viewModel::onNotificationsEnabledChange,
        onRemindersChange = viewModel::onRemindersEnabledChange,
        onLogoutClick = viewModel::onLogoutClick,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    onBiometricChange: (Boolean) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onRemindersChange: (Boolean) -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg)
            ) {
                item { ProfileHeader(name = uiState.userName, email = uiState.userEmail, initial = uiState.userInitial) }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item { AccountSection() }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item {
                    SecuritySection(
                        biometricEnabled = uiState.biometricEnabled,
                        onBiometricChange = onBiometricChange
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item {
                    NotificationsSection(
                        notificationsEnabled = uiState.notificationsEnabled,
                        remindersEnabled = uiState.remindersEnabled,
                        onNotificationsChange = onNotificationsChange,
                        onRemindersChange = onRemindersChange
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item { SupportSection() }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item { SecondaryButton(text = "Cerrar sesión", onClick = onLogoutClick) }
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, email: String, initial: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(Spacing.lg))
            Column {
                Text(text = name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(text = email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AccountSection() {
    ProfileSection(
        title = "Cuenta",
        items = listOf(
            ProfileItemData(Icons.Default.Person, "Información personal", "Editar perfil y datos", onClick = {}),
            ProfileItemData(Icons.Default.CreditCard, "Cuentas vinculadas", "Tarjetas y bancos", onClick = {})
        )
    )
}

@Composable
fun SecuritySection(biometricEnabled: Boolean, onBiometricChange: (Boolean) -> Unit) {
    ProfileSection(
        title = "Seguridad",
        items = listOf(
            ProfileItemData(Icons.Default.Lock, "Cambiar contraseña", "Actualizar tu contraseña", onClick = {}),
            ProfileItemData(Icons.Default.Settings, "Autenticación biométrica", "Usar huella dactilar o Face ID", trailing = { Switch(checked = biometricEnabled, onCheckedChange = onBiometricChange, colors = switchColors()) }, onClick = { onBiometricChange(!biometricEnabled) })
        )
    )
}

@Composable
fun NotificationsSection(notificationsEnabled: Boolean, remindersEnabled: Boolean, onNotificationsChange: (Boolean) -> Unit, onRemindersChange: (Boolean) -> Unit) {
     ProfileSection(
        title = "Notificaciones",
        items = listOf(
            ProfileItemData(Icons.Default.Notifications, "Notificaciones push", "Recibir notificaciones en el dispositivo", trailing = { Switch(checked = notificationsEnabled, onCheckedChange = onNotificationsChange, colors = switchColors()) }, onClick = { onNotificationsChange(!notificationsEnabled) }),
            ProfileItemData(Icons.Default.Settings, "Recordatorios de metas", "Notificaciones sobre progreso de ahorros", trailing = { Switch(checked = remindersEnabled, onCheckedChange = onRemindersChange, colors = switchColors()) }, onClick = { onRemindersChange(!remindersEnabled) })
        )
    )
}

@Composable
fun SupportSection() {
    ProfileSection(
        title = "Soporte",
        items = listOf(
            ProfileItemData(Icons.AutoMirrored.Filled.Help, "Preguntas frecuentes", "Encuentra respuestas rápidas", onClick = {}),
            ProfileItemData(Icons.Default.Support, "Contactar soporte", "Obtén ayuda personalizada", onClick = {})
        )
    )
}


data class ProfileItemData(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val trailing: (@Composable () -> Unit)? = null,
    val onClick: () -> Unit
)

@Composable
fun ProfileSection(title: String, items: List<ProfileItemData>) {
    Column {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = Spacing.sm))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            items.forEachIndexed { index, item ->
                CashitoListItem(
                    headline = item.title,
                    supportingText = item.subtitle,
                    leadingIcon = item.icon,
                    trailingContent = item.trailing,
                    onClick = item.onClick
                )
                if (index < items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.md))
                }
            }
        }
    }
}

@Composable
private fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
    checkedTrackColor = MaterialTheme.colorScheme.primary,
    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
)

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    CASHiTOTheme {
        ProfileScreenContent(
            uiState = ProfileUiState(
                userName = "Ana García",
                userEmail = "ana.garcia@email.com",
                userInitial = "A",
                isLoading = false
            ),
            onBiometricChange = {},
            onNotificationsChange = {},
            onRemindersChange = {},
            onLogoutClick = {},
            onNavigateBack = {}
        )
    }
}
