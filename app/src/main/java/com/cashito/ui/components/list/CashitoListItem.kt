package com.cashito.ui.components.list

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

/**
 * A standardized list item component for the Cashito app.
 *
 * @param headline The main text of the list item.
 * @param modifier Modifier for this component.
 * @param supportingText The secondary text that appears below the headline.
 * @param leadingIcon Optional icon to display at the start of the item.
 * @param leadingIconTint The color for the leading icon.
 * @param trailingContent Optional composable to display at the end of the item (e.g., a Switch or another Icon).
 * @param onClick The callback to be invoked when this item is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashitoListItem(
    headline: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    leadingIcon: ImageVector? = null,
    leadingIconTint: Color = MaterialTheme.colorScheme.primary,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    ListItem(
        headlineContent = { Text(headline, fontWeight = FontWeight.Medium) },
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        supportingContent = supportingText?.let { { Text(it, style = MaterialTheme.typography.bodySmall) } },
        leadingContent = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = headline,
                    tint = leadingIconTint
                )
            }
        },
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
