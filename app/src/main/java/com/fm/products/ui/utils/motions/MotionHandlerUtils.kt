package com.fm.products.ui.utils.motions

import android.content.Context
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.GraphicTool
import com.fm.products.ui.models.LassoSelectionState
import com.fm.products.ui.models.SelectionGraphicTool
import com.fm.products.ui.utils.selections.calculateDefaultCircleSelectionPosition
import com.fm.products.ui.utils.selections.calculateDefaultRectangleSelectionPosition
import kotlinx.coroutines.CoroutineScope
import kotlin.math.pow
import kotlin.math.sqrt


fun calculateDistanceBetweenPoints(x: Float, y: Float, x2: Float, y2: Float): Float {
    return sqrt((x - x2).pow(2) + (y - y2).pow(2))
}


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