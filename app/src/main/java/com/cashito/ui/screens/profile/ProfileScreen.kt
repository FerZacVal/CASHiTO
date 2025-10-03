package com.cashito.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cashito.ui.theme.Background
import com.cashito.ui.theme.LightGreen
import com.cashito.ui.theme.PrimaryGreen
import com.cashito.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onNavigateToLogin: () -> Unit = { navController.navigate("login") }
) {
    var biometricEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var remindersEnabled by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Perfil",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    name = "Ana García",
                    email = "ana.garcia@email.com"
                )
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Account Section
            item {
                ProfileSection(
                    title = "Cuenta",
                    items = listOf(
                        ProfileItem(
                            icon = Icons.Default.Person,
                            title = "Información personal",
                            subtitle = "Editar perfil y datos",
                            onClick = { /* Handle edit profile */ }
                        ),
                        ProfileItem(
                            icon = Icons.Default.CreditCard,
                            title = "Cuentas vinculadas",
                            subtitle = "Tarjetas y bancos",
                            onClick = { /* Handle linked accounts */ }
                        )
                    )
                )
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Security Section
            item {
                ProfileSection(
                    title = "Seguridad",
                    items = listOf(
                        ProfileItem(
                            icon = Icons.Default.Lock,
                            title = "Cambiar contraseña",
                            subtitle = "Actualizar tu contraseña",
                            onClick = { /* Handle change password */ }
                        ),
                        ProfileItem(
                            icon = Icons.Default.Settings,
                            title = "Autenticación biométrica",
                            subtitle = "Usar huella dactilar o Face ID",
                            trailing = {
                                Switch(
                                    checked = biometricEnabled,
                                    onCheckedChange = { biometricEnabled = it },
                                    colors = androidx.compose.material3.SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = PrimaryGreen,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color.Gray
                                    )
                                )
                            },
                            onClick = { /* Handle biometric toggle */ }
                        )
                    )
                )
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Notifications Section
            item {
                ProfileSection(
                    title = "Notificaciones",
                    items = listOf(
                        ProfileItem(
                            icon = Icons.Default.Notifications,
                            title = "Notificaciones push",
                            subtitle = "Recibir notificaciones en el dispositivo",
                            trailing = {
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it },
                                    colors = androidx.compose.material3.SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = PrimaryGreen,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color.Gray
                                    )
                                )
                            },
                            onClick = { /* Handle notifications toggle */ }
                        ),
                        ProfileItem(
                            icon = Icons.Default.Settings,
                            title = "Recordatorios de metas",
                            subtitle = "Notificaciones sobre progreso de ahorros",
                            trailing = {
                                Switch(
                                    checked = remindersEnabled,
                                    onCheckedChange = { remindersEnabled = it },
                                    colors = androidx.compose.material3.SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = PrimaryGreen,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color.Gray
                                    )
                                )
                            },
                            onClick = { /* Handle reminders toggle */ }
                        )
                    )
                )
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Support Section
            item {
                ProfileSection(
                    title = "Soporte",
                    items = listOf(
                        ProfileItem(
                            icon = Icons.AutoMirrored.Filled.Help,
                            title = "Preguntas frecuentes",
                            subtitle = "Encuentra respuestas rápidas",
                            onClick = { /* Handle FAQ */ }
                        ),
                        ProfileItem(
                            icon = Icons.Default.Support,
                            title = "Contactar soporte",
                            subtitle = "Obtén ayuda personalizada",
                            onClick = { /* Handle support */ }
                        )
                    )
                )
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Security Notice
            item {
                SecurityNoticeCard()
            }
            
            item { Spacer(modifier = Modifier.height(Spacing.xl)) }
            
            // Logout Button
            item {
                LogoutButton(
                    onClick = onNavigateToLogin
                )
            }
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    email: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = LightGreen,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.lg))
            
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ProfileSection(
    title: String,
    items: List<ProfileItem>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = Spacing.md)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            items.forEachIndexed { index, item ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    supportingContent = {
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = PrimaryGreen
                        )
                    },
                    trailingContent = item.trailing,
                    modifier = Modifier.clickable(onClick = item.onClick)                )
                
                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                        thickness = DividerDefaults.Thickness, color = Color.Gray.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityNoticeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LightGreen.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            Text(
                text = "🔒 Tus datos están cifrados y seguros",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LogoutButton(
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            text = "Cerrar sesión",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// Data classes
data class ProfileItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit,
    val trailing: @Composable (() -> Unit)? = null
)
