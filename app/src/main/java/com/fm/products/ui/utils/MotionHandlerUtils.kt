package com.fm.products.ui.utils

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.CircleSelectionState
import com.fm.products.ui.models.RectangleSelectionState
import com.fm.products.ui.models.SelectionTool
import com.fm.products.ui.utils.motions.CircleSelectionMotionHandler
import com.fm.products.ui.utils.motions.MotionHandler
import com.fm.products.ui.utils.motions.RectangleSelectionMotionHandler


fun createMotionHandler(
    selectionTool: SelectionTool,
    circleSelectionState: CircleSelectionState,
    onUpdateCircleSelectionState: (CircleSelectionState) -> Unit,
    rectangleSelectionState: RectangleSelectionState,
    onUpdateRectangleSelectionState: (RectangleSelectionState) -> Unit,
    imageSize: IntSize,
    imagePosition: IntOffset,
): MotionHandler<*>? {
    return when(selectionTool) {
        SelectionTool.RectangleSelection -> {
            RectangleSelectionMotionHandler(
                rectangleSelectionState = rectangleSelectionState,
                onUpdateRectangleSelectionState = onUpdateRectangleSelectionState,
                imagePosition = imagePosition,
                imageSize = imageSize,
            )
        }
        SelectionTool.CircleSelection -> {
            CircleSelectionMotionHandler(
                circleSelectionState = circleSelectionState,
                onUpdateCircleSelectionState = onUpdateCircleSelectionState,
                imagePosition = imagePosition,
                imageSize = imageSize,
            )
        }
        SelectionTool.None -> {
            null
        }
    }
}