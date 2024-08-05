package com.fm.products.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fm.products.ui.models.Tools

@Composable
fun ExportButton(
    selectedTool: Tools,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val verticalOffset by animateDpAsState(
        targetValue = if (selectedTool == Tools.None) 60.dp else 0.dp,
        label = "Export Button Animation"
    )

    Button(
        onClick = onClick,
        modifier = modifier.offset(y = verticalOffset),
    ) {
        Text(text = "Export")
    }
}
