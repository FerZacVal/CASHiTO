package com.cashito.ui.components.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.PrimaryDark
import com.cashito.ui.theme.Spacing

@Composable
fun CashitoBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.sm),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Dashboard",
                route = "dashboard",
                isSelected = currentRoute == "dashboard",
                onClick = { onNavigate("dashboard") }
            )
            
            BottomNavItem(
                icon = Icons.Default.List,
                label = "Movimientos",
                route = "transactions",
                isSelected = currentRoute == "transactions",
                onClick = { onNavigate("transactions") }
            )
            
            BottomNavItem(
                icon = Icons.Default.TrendingUp,
                label = "Metas",
                route = "goals",
                isSelected = currentRoute == "goals",
                onClick = { onNavigate("goals") }
            )
            
            BottomNavItem(
                icon = Icons.Default.AccountCircle,
                label = "Perfil",
                route = "profile",
                isSelected = currentRoute == "profile",
                onClick = { onNavigate("profile") }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    route: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(ComponentSize.iconSize)
            )
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Semibold else FontWeight.Normal
            )
        },
        selected = isSelected,
        onClick = onClick,
        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
            selectedIconColor = PrimaryDark,
            selectedTextColor = PrimaryDark,
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Gray,
            indicatorColor = Color.Transparent
        )
    )
}
