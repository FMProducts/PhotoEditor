package com.fm.products.ui.utils

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

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

fun Offset.isEmpty() = x == 0f && y == 0f
