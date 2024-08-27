package com.fm.products.ui.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import com.fm.products.ui.models.RectangleSelectionState
import com.fm.products.ui.models.RectangleSelectionState.ActivePoint


fun calculateDefaultRectangleSelectionPosition(
    drawSize: IntSize,
    drawOffset: IntOffset,
): RectangleSelectionState {
    return RectangleSelectionState(
        leftTop = calculateLeftTopPoint(drawOffset),
        leftBottom = calculateLeftBottomPoint(drawOffset, drawSize),
        rightTop = calculateRightTopPoint(drawOffset, drawSize),
        rightBottom = calculateRightBottomPoint(drawOffset, drawSize),
        imageSize = drawSize,
        imageOffset = drawOffset,
        activePoint = null,
    )
}

fun calculateLeftTopPoint(drawOffset: IntOffset): Offset {
    return Offset(
        drawOffset.x.toFloat() + 10,
        drawOffset.y.toFloat() + 10,
    )
}

fun calculateRightTopPoint(drawOffset: IntOffset, drawSize: IntSize): Offset {
    return Offset(
        drawOffset.x.toFloat() + drawSize.width - 10,
        drawOffset.y.toFloat() + 10,
    )
}

fun calculateLeftBottomPoint(drawOffset: IntOffset, drawSize: IntSize): Offset {
    return Offset(
        drawOffset.x.toFloat() + 10,
        drawOffset.y.toFloat() + drawSize.height - 10,
    )
}

fun calculateRightBottomPoint(drawOffset: IntOffset, drawSize: IntSize): Offset {
    return Offset(
        drawOffset.x.toFloat() + drawSize.width - 10,
        drawOffset.y.toFloat() + drawSize.height - 10,
    )
}

fun DrawScope.drawRectangleSelection(position: RectangleSelectionState) {

    drawRect(
        color = Color.LightGray,
        topLeft = position.imageOffset.toOffset(),
        style = dashStyle(),
        size = position.imageSize.toSize()
    )

    drawCircle(
        color = pointColor(position.activePoint == ActivePoint.LEFT_TOP),
        radius = 20f,
        center = position.leftTop,
    )


    drawCircle(
        color = pointColor(position.activePoint == ActivePoint.RIGHT_TOP),
        radius = 20f,
        center = position.rightTop
    )

    drawCircle(
        color = pointColor(position.activePoint == ActivePoint.LEFT_BOTTOM),
        radius = 20f,
        center = position.leftBottom
    )

    drawCircle(
        color = pointColor(position.activePoint == ActivePoint.RIGHT_BOTTOM),
        radius = 20f,
        center = position.rightBottom,
    )
}
