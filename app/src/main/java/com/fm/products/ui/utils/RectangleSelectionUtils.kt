package com.fm.products.ui.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import com.fm.products.ui.models.RectangleSelectionPosition


fun calculateDefaultRectangleSelectionPosition(
    drawSize: IntSize,
    drawOffset: IntOffset,
): RectangleSelectionPosition {
    return RectangleSelectionPosition(
        leftTop = leftTopCircleOffset(drawOffset),
        leftBottom = leftBottomCircleOffset(drawOffset, drawSize),
        rightTop = rightTopCircleOffset(drawOffset, drawSize),
        rightBottom = rightBottomCircleOffset(drawOffset, drawSize),
        drawSize = drawSize,
        drawOffset = drawOffset,
    )
}

private fun leftTopCircleOffset(drawOffset: IntOffset): Offset {
    return Offset(
        drawOffset.x.toFloat() + 10,
        drawOffset.y.toFloat() + 10,
    )
}

private fun rightTopCircleOffset(drawOffset: IntOffset, drawSize: IntSize): Offset {
    return Offset(
        drawOffset.x.toFloat() + drawSize.width - 10,
        drawOffset.y.toFloat() + 10,
    )
}

private fun leftBottomCircleOffset(drawOffset: IntOffset, drawSize: IntSize): Offset {
    return Offset(
        drawOffset.x.toFloat() + 10,
        drawOffset.y.toFloat() + drawSize.height - 10,
    )
}

private fun rightBottomCircleOffset(drawOffset: IntOffset, drawSize: IntSize): Offset {
    return Offset(
        drawOffset.x.toFloat() + drawSize.width - 10,
        drawOffset.y.toFloat() + drawSize.height - 10,
    )
}

fun DrawScope.drawRectangleSelection(position: RectangleSelectionPosition) {

    drawRect(
        color = Color.LightGray,
        topLeft = position.drawOffset.toOffset(),
        style = dashStyle(),
        size = position.drawSize.toSize()
    )

    drawCircle(
        color = Color.Magenta,
        radius = 20f,
        center = position.leftTop,
    )


    drawCircle(
        color = Color.Magenta,
        radius = 20f,
        center = position.rightTop
    )

    drawCircle(
        color = Color.Magenta,
        radius = 20f,
        center = position.leftBottom
    )

    drawCircle(
        color = Color.Magenta,
        radius = 20f,
        center = position.rightBottom,
    )
}
