package com.fm.products.ui.utils.cropper

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.IntOffset
import com.fm.products.ui.models.CircleSelectionState

class CircleCropper(
    private val circleSelectionState: CircleSelectionState,
    private val image: ImageBitmap,
    private val canvasSize: Size,
    private val imageOffset: IntOffset,
) {

    fun crop(): Bitmap {
        val srcBitmap = image.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)

        val scaleFactor = getScaleFactor(srcBitmap)

        val result = createEmptyBitmap(scaleFactor)
        val canvas = Canvas(result).nativeCanvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val radius = circleSelectionState.radius * scaleFactor

        canvas.drawCircle(
            radius, // center x
            radius, // center y
            radius, // radius
            paint,
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))

        val x = calculateX(scaleFactor)
        val y = calculateY(scaleFactor)
        val widthHeight = calculateWidthHeight(scaleFactor)

        canvas.drawBitmap(
            srcBitmap,
            Rect(
                x, y,
                x + widthHeight,
                y + widthHeight,
            ),
            Rect(
                0,
                0,
                widthHeight,
                widthHeight,
            ),
            paint,
        )

        return result.asAndroidBitmap()
    }

    private fun calculateX(scale: Float): Int {
        return ((circleSelectionState.center.x - circleSelectionState.radius) * scale).toInt()
    }

    private fun calculateY(scale: Float): Int {
        val y = circleSelectionState.center.y - circleSelectionState.radius - imageOffset.y
        return ((y) * scale).toInt()
    }

    private fun calculateWidthHeight(scale: Float): Int {
        return (circleSelectionState.radius * 2 * scale).toInt()
    }

    private fun getScaleFactor(srcBitmap: Bitmap): Float {

        val scaleFactorWidth = srcBitmap.width / canvasSize.width
        val scaleFactorHeight = srcBitmap.height / canvasSize.height
        return maxOf(scaleFactorHeight, scaleFactorWidth)
    }

    private fun createEmptyBitmap(scale: Float): ImageBitmap {
        val widthHeight = (circleSelectionState.radius * 2 * scale).toInt()
        return Bitmap.createBitmap(widthHeight, widthHeight, Bitmap.Config.ARGB_8888)
            .asImageBitmap()
    }
}
