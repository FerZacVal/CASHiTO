package com.cashito.ui.components.inputs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.cashito.ui.theme.BorderColor
import com.cashito.ui.theme.ComponentSize
import com.cashito.ui.theme.Error
import com.cashito.ui.theme.PrimaryGreen
import com.cashito.ui.theme.Radius
import com.cashito.ui.theme.Spacing

@Composable
fun CashitoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .height(ComponentSize.inputHeight),
            enabled = enabled,
            isError = isError,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = if (isError) Error else PrimaryGreen,
                unfocusedBorderColor = if (isError) Error else BorderColor,
                errorBorderColor = Error,
                focusedLabelColor = if (isError) Error else PrimaryGreen,
                unfocusedLabelColor = if (isError) Error else Color.Gray,
                errorLabelColor = Error
            ),
            shape = RoundedCornerShape(Radius.md),
            singleLine = true
        )
        
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Error,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = Spacing.xs)
            )
        }
    }
}

@Composable
fun CashitoSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        modifier = modifier
            .fillMaxWidth()
            .height(ComponentSize.inputHeight),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = PrimaryGreen,
            unfocusedBorderColor = BorderColor,
            focusedLabelColor = PrimaryGreen,
            unfocusedLabelColor = Color.Gray
        ),
        shape = RoundedCornerShape(Radius.md),
        singleLine = true,
        leadingIcon = {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                contentDescription = "Search"
            )
        }
    )
}
