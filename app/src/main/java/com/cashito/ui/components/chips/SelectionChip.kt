package com.cashito.ui.components.chips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cashito.ui.theme.Radius
import com.cashito.ui.theme.Spacing

/**
 * A generic, reusable chip for selection purposes.
 *
 * @param title The main text to display on the chip.
 * @param icon The emoji or icon to display.
 * @param color The base color of the chip.
 * @param isSelected Whether the chip is currently selected.
 * @param onClick The callback to be invoked when this chip is clicked.
 * @param modifier The modifier to be applied to the chip.
 */
@Composable
fun SelectionChip(
    title: String,
    icon: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(Radius.round)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else color
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else color,
                textAlign = TextAlign.Center
            )
        }
    }
}
