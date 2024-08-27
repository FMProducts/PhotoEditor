package com.fm.products.ui.utils.cropper

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.IntOffset
import com.fm.products.ui.models.CircleSelectionState
import com.fm.products.ui.models.GraphicTool
import com.fm.products.ui.models.LassoSelectionState
import com.fm.products.ui.models.OtherGraphicTool
import com.fm.products.ui.models.RectangleSelectionState
import com.fm.products.ui.models.SelectionState

fun cropImage(
    image: ImageBitmap,
    graphicTool: GraphicTool,
    selectionState: SelectionState?,
    canvasSize: Size,
    imagePosition: IntOffset,
): Bitmap? {
    return when (graphicTool) {
        OtherGraphicTool.BackgroundRemover -> {
            image.asAndroidBitmap()
        }

        is OtherGraphicTool.PhotoFilter -> {
            image.asAndroidBitmap()
        }

        else -> {
            cropImageByState(
                image = image,
                selectionState = selectionState,
                canvasSize = canvasSize,
                imagePosition = imagePosition,
            )
        }
    }
}

private fun cropImageByState(
    image: ImageBitmap,
    selectionState: SelectionState?,
    canvasSize: Size,
    imagePosition: IntOffset,
): Bitmap? {
    val cropper: ImageCropper? = when (selectionState) {
        is RectangleSelectionState -> {
            RectangleCropper(
                rectangleSelectionState = selectionState,
                image = image,
                canvasSize = canvasSize,
                imageOffset = imagePosition
            )
        }

        is CircleSelectionState -> {
            CircleCropper(
                circleSelectionState = selectionState,
                image = image,
                canvasSize = canvasSize,
                imageOffset = imagePosition
            )
        }

        is LassoSelectionState -> {
            LassoCropper(
                lassoSelectionState = selectionState,
                image = image,
                canvasSize = canvasSize,
                imageOffset = imagePosition,
            )
        }

        else -> {
            null
        }
    }
    return cropper?.crop()
}
