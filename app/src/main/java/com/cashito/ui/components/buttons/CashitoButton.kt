package com.cashito.ui.components.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.PrimaryGreen
import com.cashito.ui.theme.PrimaryDark
import com.cashito.ui.theme.Radius

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ComponentSize.buttonHeight),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryGreen,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
        ),
        shape = RoundedCornerShape(Radius.md)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Semibold
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ComponentSize.buttonHeight),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryDark
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (enabled) PrimaryGreen else Color.Gray
        ),
        shape = RoundedCornerShape(Radius.md)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Semibold
        )
    }
}

@Composable
fun SmallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true
) {
    val buttonColors = if (isPrimary) {
        ButtonDefaults.buttonColors(
            containerColor = PrimaryGreen,
            contentColor = Color.White
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = PrimaryDark
        )
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(ComponentSize.chipHeight),
        enabled = enabled,
        colors = buttonColors,
        shape = RoundedCornerShape(Radius.round)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
