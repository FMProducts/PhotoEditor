package com.fm.products.ui.utils

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
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

fun dashStyle(): Stroke {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    return Stroke(width = 10f, pathEffect = pathEffect)
}

fun leftTopCircleOffset(drawOffset: IntOffset): Offset {
    return Offset(
        drawOffset.x.toFloat() + 10,
        drawOffset.y.toFloat() + 10,
    )
}

fun rightTopCircleOffset(drawOffset: IntOffset, drawSize: IntSize): Offset {
    return Offset(
        drawOffset.x.toFloat() + drawSize.width - 10,
        drawOffset.y.toFloat() + 10,
    )
}

fun leftBottomCircleOffset(drawOffset: IntOffset, drawSize: IntSize): Offset {
    return Offset(
        drawOffset.x.toFloat() + 10,
        drawOffset.y.toFloat() + drawSize.height - 10,
    )
}

fun rightBottomCircleOffset(drawOffset: IntOffset, drawSize: IntSize): Offset {
    return Offset(
        drawOffset.x.toFloat() + drawSize.width - 10,
        drawOffset.y.toFloat() + drawSize.height - 10,
    )
}