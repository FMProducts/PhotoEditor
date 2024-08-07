package com.fm.products.ui.utils

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.pow
import kotlin.math.sqrt

fun Uri.toImageBitmap(context: Context): ImageBitmap {
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    }
    return bitmap.asImageBitmap()
}

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

fun emptyOffset() = Offset(0f, 0f)

fun emptyIntOffset() = IntOffset(0, 0)

fun emptyIntSize() = IntSize(0, 0)

fun emptySize() = Size(0f, 0f)

fun Offset.isEmpty() = x == 0f && y == 0f

fun IntOffset.isEmpty() = x == 0 && y == 0

fun IntSize.isEmpty() = height == 0 && width == 0

fun Boolean?.orFalse() = this ?: false

fun pointColor(isActive: Boolean) = if (isActive) Color.Blue else Color.Magenta

fun dashStyle(): Stroke {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    return Stroke(width = 10f, pathEffect = pathEffect)
}

fun calculateDistanceBetweenPoints(x: Float, y: Float, x2: Float, y2: Float): Float {
    return sqrt((x - x2).pow(2) + (y - y2).pow(2))
}
