package com.fm.products.ui.utils.cropper

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.IntOffset
import com.fm.products.ui.models.LassoSelectionState
import android.graphics.Canvas as NativeCanvas

class LassoCropper(
    private val lassoSelectionState: LassoSelectionState,
    private val image: ImageBitmap,
    private val canvasSize: Size,
    private val imageOffset: IntOffset,
) : ImageCropper {

    override fun crop(): Bitmap? {
        if (lassoSelectionState.isEmpty()) return null
        val srcBitmap = image.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)

        val scaleFactor = getScaleFactor(srcBitmap)

        val result = createEmptyBitmap(scaleFactor)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas(result).nativeCanvas

        canvas.drawPoints(scaleFactor, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawImage(srcBitmap, scaleFactor, paint)

        return result.asAndroidBitmap()
    }

    private fun NativeCanvas.drawPoints(
        scale: Float,
        paint: Paint,
    ) {
        val points = lassoSelectionState.points
        val dstOffset = calculateDestinationImageOffset()
        val firstPoint = points.first()
        val path = Path()
        path.reset()
        path.moveTo(
            (firstPoint.x - dstOffset.x) * scale,
            (firstPoint.y - dstOffset.y) * scale,
        )

        for (p in points) {
            if (p == firstPoint) continue
            path.lineTo(
                (p.x - dstOffset.x) * scale,
                (p.y - dstOffset.y) * scale,
            )
        }
        this.drawPath(path, paint)
    }

    private fun NativeCanvas.drawImage(srcBitmap: Bitmap, scale: Float, paint: Paint) {

        val x = (calculateLeftPoint() - imageOffset.x) * scale
        val y = (calculateTopPoint() - imageOffset.y) * scale
        val width = calculateWidth() * scale
        val height = calculateHeight() * scale

        val srcRect = Rect(
            x.toInt(),
            y.toInt(),
            (x + width).toInt(),
            (y + height).toInt(),
        )

        this.drawBitmap(
            srcBitmap,
            srcRect,
            RectF(
                0f,
                0f,
                width,
                height,
            ),
            paint
        )
    }

    private fun calculateLeftPoint() = lassoSelectionState.points.minOf { it.x }

    private fun calculateTopPoint() = lassoSelectionState.points.minOf { it.y }

    private fun calculateRightPoint() = lassoSelectionState.points.maxOf { it.x }

    private fun calculateBottomPoint() = lassoSelectionState.points.maxOf { it.y }

    private fun calculateWidth() = calculateRightPoint() - calculateLeftPoint()

    private fun calculateHeight() = calculateBottomPoint() - calculateTopPoint()

    private fun getScaleFactor(srcBitmap: Bitmap): Float {
        val scaleFactorWidth = srcBitmap.width / canvasSize.width
        val scaleFactorHeight = srcBitmap.height / canvasSize.height
        return maxOf(scaleFactorHeight, scaleFactorWidth)
    }

    private fun calculateDestinationImageOffset(): Offset {
        val leftPoint = calculateLeftPoint()
        val topPoint = calculateTopPoint()
        return Offset(leftPoint, topPoint)
    }

    private fun createEmptyBitmap(scale: Float): ImageBitmap {
        val width = calculateWidth() * scale
        val height = calculateHeight() * scale
        return Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
            .asImageBitmap()
    }
}
