package com.fm.products.ui.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize

@Composable
private fun CanvasPlayground() {

    var imageSize: IntSize by remember {
        mutableStateOf(emptyIntSize())
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (imageSize.isEmpty()) {
                imageSize = IntSize(size.width.toInt(), size.height.toInt())
            }
        }
    }
}


@Preview
@Composable
private fun CanvasPlaygroundPreview() {
    CanvasPlayground()
}
