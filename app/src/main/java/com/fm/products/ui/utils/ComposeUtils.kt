package com.fm.products.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.fm.products.ui.theme.Pink80

@Composable
fun disabledColor(): Color {
    return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
}

@Composable
fun buttonStateColor(isEnabled: Boolean): Color {
    return if (isEnabled) Pink80 else disabledColor()
}
