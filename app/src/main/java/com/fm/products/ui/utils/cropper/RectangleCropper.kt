package com.fm.products.ui.utils.cropper

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.RectangleSelectionState

class RectangleCropper(
    private val rectangleSelectionState: RectangleSelectionState,
    private val image: ImageBitmap,
    private val canvasSize: Size,
    private val imageOffset: IntOffset,
) : ImageCropper {

    override fun crop(): Bitmap? {
        if (rectangleSelectionState.isEmpty()) return null
        val srcBitmap = image.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)

        val scaleFactor = getScaleFactor(srcBitmap)
        val selectionSize = calculateSelectionSizeInPx(scaleFactor)

        val result = createEmptyBitmap(selectionSize.width, selectionSize.height)
        val canvas = Canvas(result)

        val paint = Paint()

        canvas.drawImageRect(
            srcBitmap.asImageBitmap(),
            paint = paint,
            srcOffset = IntOffset(
                (rectangleSelectionState.imageOffset.x * scaleFactor).toInt(),
                ((rectangleSelectionState.imageOffset.y - imageOffset.y) * scaleFactor).toInt()
            )
        )

        return result.asAndroidBitmap()
    }

    private fun calculateSelectionSizeInPx(scaleFactor: Float): IntSize {
        val width = rectangleSelectionState.imageSize.width * scaleFactor
        val height = rectangleSelectionState.imageSize.height * scaleFactor
        return IntSize(width.toInt(), height.toInt())
    }

    private fun getScaleFactor(srcBitmap: Bitmap): Float {

        val scaleFactorWidth = srcBitmap.width / canvasSize.width
        val scaleFactorHeight = srcBitmap.height / canvasSize.height
        return maxOf(scaleFactorHeight, scaleFactorWidth)
    }

    private fun createEmptyBitmap(width: Int, height: Int) =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).asImageBitmap()
}
