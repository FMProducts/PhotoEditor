package com.fm.products.ui.utils

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import com.fm.products.ui.models.CircleSelectionState
import com.fm.products.ui.models.LassoSelectionState
import com.fm.products.ui.models.RectangleSelectionState
import com.fm.products.ui.models.SelectionState
import com.fm.products.ui.utils.cropper.CircleCropper
import com.fm.products.ui.utils.cropper.ImageCropper
import com.fm.products.ui.utils.cropper.RectangleCropper


fun DrawScope.drawSelectionByState(selectionState: SelectionState) {
    when (selectionState) {
        is RectangleSelectionState -> {
            drawRectangleSelection(selectionState)
        }

        is CircleSelectionState -> {
            drawCircleSelection(selectionState)
        }

        is LassoSelectionState -> {
            drawLassoSelection(selectionState)
        }
    }
}

fun ImageBitmap.cropByState(
    selectionState: SelectionState,
    canvasSize: Size,
    imagePosition: IntOffset,
): Bitmap? {
    val cropper: ImageCropper? = when (selectionState) {
        is RectangleSelectionState -> {
            RectangleCropper(
                rectangleSelectionState = selectionState,
                image = this,
                canvasSize = canvasSize,
                imageOffset = imagePosition
            )
        }

        is CircleSelectionState -> {
            CircleCropper(
                circleSelectionState = selectionState,
                image = this,
                canvasSize = canvasSize,
                imageOffset = imagePosition
            )
        }

        is LassoSelectionState -> {
            // TODO
            null
        }

        else -> {
            null
        }
    }
    return cropper?.crop()
}
