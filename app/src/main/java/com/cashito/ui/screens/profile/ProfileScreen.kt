package com.cashito.ui.screens.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cashito.ui.components.buttons.SecondaryButton
import com.cashito.ui.theme.Spacing
import com.cashito.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(),
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onNavigateToLogin: () -> Unit = { navController.navigate("login") { popUpTo(navController.graph.id) { inclusive = true } } }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) {
            onNavigateToLogin()
        }
    }

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
                        onBiometricChange = viewModel::onBiometricEnabledChange
                    )
                }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item { 
                    NotificationsSection(
                        notificationsEnabled = uiState.notificationsEnabled,
                        remindersEnabled = uiState.remindersEnabled,
                        onNotificationsChange = viewModel::onNotificationsEnabledChange,
                        onRemindersChange = viewModel::onRemindersEnabledChange
                    )
                }
                
                item { Spacer(modifier = Modifier.height(Spacing.xl)) }
                
                item { SupportSection() }

                item { Spacer(modifier = Modifier.height(Spacing.xl)) }

                item { SecondaryButton(text = "Cerrar sesión", onClick = viewModel::onLogoutClick) }
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
            ProfileItemData(
                icon = Icons.Default.Person,
                title = "Información personal",
                subtitle = "Editar perfil y datos",
                onClick = { /* Handle edit profile */ }
            ),
            ProfileItemData(
                icon = Icons.Default.CreditCard,
                title = "Cuentas vinculadas",
                subtitle = "Tarjetas y bancos",
                onClick = { /* Handle linked accounts */ }
            )
        )
    )
}

@Composable
fun SecuritySection(biometricEnabled: Boolean, onBiometricChange: (Boolean) -> Unit) {
    ProfileSection(
        title = "Seguridad",
        items = listOf(
            ProfileItemData(
                icon = Icons.Default.Lock,
                title = "Cambiar contraseña",
                subtitle = "Actualizar tu contraseña",
                onClick = { /* Handle change password */ }
            ),
            ProfileItemData(
                icon = Icons.Default.Settings,
                title = "Autenticación biométrica",
                subtitle = "Usar huella dactilar o Face ID",
                trailing = { Switch(checked = biometricEnabled, onCheckedChange = onBiometricChange, colors = switchColors()) },
                onClick = { onBiometricChange(!biometricEnabled) }
            )
        )
    )
}

@Composable
fun NotificationsSection(notificationsEnabled: Boolean, remindersEnabled: Boolean, onNotificationsChange: (Boolean) -> Unit, onRemindersChange: (Boolean) -> Unit) {
     ProfileSection(
        title = "Notificaciones",
        items = listOf(
            ProfileItemData(
                icon = Icons.Default.Notifications,
                title = "Notificaciones push",
                subtitle = "Recibir notificaciones en el dispositivo",
                trailing = { Switch(checked = notificationsEnabled, onCheckedChange = onNotificationsChange, colors = switchColors()) },
                onClick = { onNotificationsChange(!notificationsEnabled) }
            ),
            ProfileItemData(
                icon = Icons.Default.Settings,
                title = "Recordatorios de metas",
                subtitle = "Notificaciones sobre progreso de ahorros",
                trailing = { Switch(checked = remindersEnabled, onCheckedChange = onRemindersChange, colors = switchColors()) },
                onClick = { onRemindersChange(!remindersEnabled) }
            )
        )
    )
}

@Composable
fun SupportSection() {
    ProfileSection(
        title = "Soporte",
        items = listOf(
            ProfileItemData(
                icon = Icons.AutoMirrored.Filled.Help,
                title = "Preguntas frecuentes",
                subtitle = "Encuentra respuestas rápidas",
                onClick = { /* Handle FAQ */ }
            ),
            ProfileItemData(
                icon = Icons.Default.Support,
                title = "Contactar soporte",
                subtitle = "Obtén ayuda personalizada",
                onClick = { /* Handle support */ }
            )
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
                ListItem(
                    headlineContent = { Text(item.title, fontWeight = FontWeight.Medium) },
                    supportingContent = { Text(item.subtitle, style = MaterialTheme.typography.bodySmall) },
                    leadingContent = { Icon(item.icon, contentDescription = item.title, tint = MaterialTheme.colorScheme.primary) },
                    trailingContent = item.trailing,
                    modifier = Modifier.clickable(onClick = item.onClick),
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
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
