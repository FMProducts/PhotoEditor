package com.fm.products.ui.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.LassoSelectionState
import com.fm.products.ui.models.SelectionTool
import com.fm.products.ui.utils.motions.CircleSelectionMotionHandler
import com.fm.products.ui.utils.motions.EmptyMotionHandler
import com.fm.products.ui.utils.motions.LassoSelectionMotionHandler
import com.fm.products.ui.utils.motions.MotionHandler
import com.fm.products.ui.utils.motions.RectangleSelectionMotionHandler


fun createMotionHandler(
    selectionTool: SelectionTool,
    imageSize: IntSize,
    imagePosition: IntOffset,
    canvasCenter: Offset,
): MotionHandler {
    return when (selectionTool) {
        SelectionTool.RectangleSelection -> {
            RectangleSelectionMotionHandler(
                rectangleSelectionState = calculateDefaultRectangleSelectionPosition(
                    drawSize = imageSize,
                    drawOffset = imagePosition
                ),
                imagePosition = imagePosition,
                imageSize = imageSize,
            )
        }

        SelectionTool.CircleSelection -> {
            CircleSelectionMotionHandler(
                circleSelectionState = calculateDefaultCircleSelectionPosition(
                    drawSize = imageSize,
                    center = canvasCenter
                ),
                imagePosition = imagePosition,
                imageSize = imageSize,
            )
        }

        SelectionTool.LassoSelection -> {
            LassoSelectionMotionHandler(
                lassoSelectionState = LassoSelectionState(),
                imagePosition = imagePosition,
                imageSize = imageSize
            )
        }

        SelectionTool.None -> {
            EmptyMotionHandler()
        }
    }
}