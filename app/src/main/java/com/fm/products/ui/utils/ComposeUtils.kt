package com.fm.products.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.theme.Pink80

@Composable
fun disabledColor(): Color {
    return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
}

@Composable
fun buttonStateColor(isEnabled: Boolean): Color {
    return if (isEnabled) Pink80 else disabledColor()
}

fun dashStyle(): Stroke {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    return Stroke(width = 10f, pathEffect = pathEffect)
}

fun pointColor(isActive: Boolean) = if (isActive) Color.Blue else Color.Magenta


fun emptyOffset() = Offset(0f, 0f)

fun emptyIntOffset() = IntOffset(0, 0)

fun emptyIntSize() = IntSize(0, 0)

fun emptySize() = Size(0f, 0f)

fun Offset.isEmpty() = x == 0f && y == 0f

fun IntOffset.isEmpty() = x == 0 && y == 0

fun IntSize.isEmpty() = height == 0 && width == 0


fun calculateDrawImageSize(
    canvasWidth: Float,
    canvasHeight: Float,
    imageWidth: Float,
    imageHeight: Float,
): IntSize {
    // Вычисление коэффициентов масштабирования для Canvas и изображения
    val scaleX = canvasWidth / imageWidth
    val scaleY = canvasHeight / imageHeight
    val scale = minOf(scaleX, scaleY)

    // Вычисление размеров и позиции изображения на Canvas
    val dstWidth = scale * imageWidth
    val dstHeight = scale * imageHeight
    return IntSize(dstWidth.toInt(), dstHeight.toInt())
}

fun calculateDrawImageOffset(

    canvasWidth: Float,
    canvasHeight: Float,
    drawImageSize: IntSize,
): IntOffset {
    val offsetX = (canvasWidth - drawImageSize.width) / 2
    val offsetY = (canvasHeight - drawImageSize.height) / 2
    return IntOffset(offsetX.toInt(), offsetY.toInt())
}
