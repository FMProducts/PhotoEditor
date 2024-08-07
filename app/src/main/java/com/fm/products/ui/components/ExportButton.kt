package com.fm.products.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fm.products.ui.models.SelectionTool

@Composable
fun ExportButton(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val verticalOffset by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 120.dp,
        label = "Export Button Animation"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Button(
            onClick = onClick,
            modifier = modifier.offset(y = verticalOffset).align(Alignment.BottomCenter),
        ) {
            Text(text = "Export")
        }
    }
}
