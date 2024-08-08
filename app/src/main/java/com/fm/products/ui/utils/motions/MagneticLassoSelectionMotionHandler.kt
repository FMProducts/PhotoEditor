package com.fm.products.ui.utils.motions

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.LassoSelectionState
import com.fm.products.ui.utils.cropper.LassoCropper

class MagneticLassoSelectionMotionHandler(
    lassoSelectionState: LassoSelectionState,
    imagePosition: IntOffset,
    imageSize: IntSize,
    val canvasSize: Size,
    val sourceImage: ImageBitmap,
) : LassoSelectionMotionHandler(
    lassoSelectionState = lassoSelectionState,
    imagePosition = imagePosition,
    imageSize = imageSize,
) {

    var changeProgressBarState: ((Boolean) -> Unit)? = null

    override fun drawActionUp(x: Float, y: Float) {
        super.drawActionUp(x, y)

        changeProgressBarState?.invoke(true)
        val cropper = LassoCropper(
            lassoSelectionState = selectionState.value,
            image = sourceImage,
            canvasSize = canvasSize,
            imageOffset = imagePosition,
        )
        val image = cropper.crop()
        changeProgressBarState?.invoke(false)
    }

}