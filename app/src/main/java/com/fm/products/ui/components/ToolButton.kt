package com.fm.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.fm.products.ui.utils.buttonStateColor

@Composable
fun ToolButton(
    icon: Painter,
    modifier: Modifier = Modifier,
    isEnable: Boolean = true,
    onClick: () -> Unit,
) {
    val color = buttonStateColor(isEnable)
    IconButton(
        onClick = onClick,
        modifier = modifier
            .background(color, RoundedCornerShape(12.dp))
    ) {
        Icon(
            painter = icon,
            contentDescription = ""
        )
    }
}
