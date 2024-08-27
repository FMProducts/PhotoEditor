package com.fm.products.ui.utils

import android.content.Context
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.utils.motions.MagneticLassoSelectionMotionHandler
import com.fm.products.ui.models.LassoSelectionState
import com.fm.products.ui.models.SelectionTool
import com.fm.products.ui.utils.motions.CircleSelectionMotionHandler
import com.fm.products.ui.utils.motions.EmptyMotionHandler
import com.fm.products.ui.utils.motions.LassoSelectionMotionHandler
import com.fm.products.ui.utils.motions.MotionHandler
import com.fm.products.ui.utils.motions.RectangleSelectionMotionHandler
import kotlinx.coroutines.CoroutineScope


fun createMotionHandler(
    graphicTool: GraphicTool,
    imageSize: IntSize,
    imagePosition: IntOffset,
    canvasSize: Size,
    sourceImage: ImageBitmap,
    coroutineScope: CoroutineScope,
    context: Context,
): MotionHandler {
    return when (graphicTool) {
        SelectionGraphicTool.RectangleSelection -> {
            RectangleSelectionMotionHandler(
                rectangleSelectionState = calculateDefaultRectangleSelectionPosition(
                    drawSize = imageSize,
                    drawOffset = imagePosition
                ),
                imagePosition = imagePosition,
                imageSize = imageSize,
            )
        }

        SelectionGraphicTool.CircleSelection -> {
            CircleSelectionMotionHandler(
                circleSelectionState = calculateDefaultCircleSelectionPosition(
                    drawSize = imageSize,
                    center = canvasSize.center
                ),
                imagePosition = imagePosition,
                imageSize = imageSize,
            )
        }

        SelectionGraphicTool.LassoSelection -> {
            LassoSelectionMotionHandler(
                lassoSelectionState = LassoSelectionState(),
                imagePosition = imagePosition,
                imageSize = imageSize
            )
        }

        SelectionGraphicTool.MagneticLassoSelection -> {
            MagneticLassoSelectionMotionHandler(
                lassoSelectionState = LassoSelectionState(),
                imagePosition = imagePosition,
                imageSize = imageSize,
                canvasSize = canvasSize,
                sourceImage = sourceImage,
                coroutineScope = coroutineScope,
                context = context,
            )
        }

        else -> {
            EmptyMotionHandler()
        }
    }
}