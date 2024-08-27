package com.fm.products.ui.utils.motions

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.LassoSelectionState
import com.fm.products.ui.utils.cropper.RectangleCropper
import com.fm.products.ui.utils.finContours
import com.fm.products.ui.utils.findMaxByArea
import com.fm.products.ui.utils.processors.removeBackground
import com.fm.products.ui.utils.processToCanny
import com.fm.products.ui.utils.processToGray
import com.fm.products.ui.utils.selections.calculateLeftPoint
import com.fm.products.ui.utils.selections.calculateTopPoint
import com.fm.products.ui.utils.selections.mapToRectangleSelectionState
import com.fm.products.ui.utils.toMat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint

class MagneticLassoSelectionMotionHandler(
    lassoSelectionState: LassoSelectionState,
    imagePosition: IntOffset,
    imageSize: IntSize,
    val canvasSize: Size,
    val sourceImage: ImageBitmap,
    val coroutineScope: CoroutineScope,
    val context: Context,
) : LassoSelectionMotionHandler(
    lassoSelectionState = lassoSelectionState,
    imagePosition = imagePosition,
    imageSize = imageSize,
) {

    init {
        OpenCVLoader.initLocal()
    }

    var changeProgressBarState: ((Boolean) -> Unit)? = null

    override fun drawActionUp(x: Float, y: Float) {
        super.drawActionUp(x, y)

        coroutineScope.launch {
            changeProgressBarState?.invoke(true)

            val image = crop()
            image?.let {

                val imgMat = processImage(it)
                val contour = calculateContour(imgMat)
                updateStateByContour(contour)
            }
            changeProgressBarState?.invoke(false)
        }
    }

    private fun updateStateByContour(contour: MatOfPoint?) {
        val matPoints = contour?.toList() ?: return
        val lassoSelectionState = selectionState.value.filterPointsInFrame()

        val imageScale = calculateImageScale()

        val offset = IntOffset(
            lassoSelectionState.calculateLeftPoint().toInt(),
            lassoSelectionState.calculateTopPoint().toInt(),
        )

        val statePoints = mutableListOf<LassoSelectionState.Point>()
        for (mp in matPoints) {
            statePoints.add(
                LassoSelectionState.Point(
                    x = (mp.x * imageScale).toFloat() + offset.x,
                    y = (mp.y * imageScale).toFloat() + offset.y,
                    direction = LassoSelectionState.PointDirection.UNDEFINED,
                )
            )
        }
        _selectionState.update {
            it.copy(points = statePoints)
        }
    }

    private suspend fun processImage(bitmap: Bitmap): Mat = withContext(Dispatchers.Default) {
        val objectBitmap = removeBackground(bitmap, context)
        val image = objectBitmap ?: bitmap
        val src = image.toMat()

        val gray = processToGray(src)

        if (objectBitmap == null) processToCanny(gray) else gray
    }

    private suspend fun calculateContour(imgMat: Mat) = withContext(Dispatchers.Default) {
        val contours = finContours(imgMat)
        if (contours.isNotEmpty()) contours.findMaxByArea() else null
    }

    private fun crop(): Bitmap? {
        if (selectionState.value.points.size < 3) return null

        val cropper = RectangleCropper(
            selectionState.value.filterPointsInFrame().mapToRectangleSelectionState(),
            sourceImage,
            canvasSize,
            imagePosition,
        )

        return cropper.crop()
    }

    private fun LassoSelectionState.filterPointsInFrame(): LassoSelectionState {
        val top = imagePosition.y
        val bottom = imagePosition.y + imageSize.height
        val left = imagePosition.x
        val right = imagePosition.x + imageSize.width

        val newPoints = points.filter {
            it.y > top && it.y < bottom && it.x > left && it.x < right
        }
        return copy(points = newPoints)
    }

    private fun calculateImageScale(): Float {
        val scaleFactorWidth = canvasSize.width / sourceImage.width
        val scaleFactorHeight = canvasSize.height / sourceImage.height
        return minOf(scaleFactorHeight, scaleFactorWidth)
    }
}