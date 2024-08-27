package com.fm.products.ui.utils.selections

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.LassoSelectionState
import com.fm.products.ui.models.LassoSelectionState.Point
import com.fm.products.ui.models.LassoSelectionState.PointDirection
import com.fm.products.ui.models.RectangleSelectionState
import com.fm.products.ui.utils.dashStyle
import com.fm.products.ui.utils.pointColor

fun DrawScope.drawLassoSelection(position: LassoSelectionState) {
    if (position.isEmpty() || position.points.size == 1) return

    val pointsToDraw = mutableListOf<Point>()

    val firstPoint = position.points.first()
    val path = Path()
    path.reset()
    path.moveTo(firstPoint.x, firstPoint.y)
    pointsToDraw.add(firstPoint)

    var prevPoint = firstPoint
    for (p in position.points) {
        if (p == firstPoint) continue

        path.lineTo(p.x, p.y)
        if (prevPoint.direction != p.direction) {
            pointsToDraw.add(prevPoint)
        }
        prevPoint = p
    }

    if (position.isDraw.not() || prevPoint.isLastPoint) {
        path.lineTo(firstPoint.x, firstPoint.y)
        pointsToDraw.add(prevPoint)
    }

    drawPath(
        path = path,
        color = Color.LightGray,
        style = dashStyle(),
    )

    if (position.isDrawVisualPoints) {
        drawPoints(pointsToDraw)
    }
}

private fun DrawScope.drawPoints(points: List<Point>) {
    for (p in points) {
        drawCircle(
            color = pointColor(false),
            radius = 20f,
            center = Offset(p.x, p.y),
        )
    }
}

fun LassoSelectionState.calculateLeftPoint(): Float = points.minOf { it.x }

fun LassoSelectionState.calculateTopPoint(): Float = points.minOf { it.y }

fun LassoSelectionState.calculateWidth(): Float = points.maxOf { it.x } - points.minOf { it.x }
fun LassoSelectionState.calculateHeight(): Float = points.maxOf { it.y } - points.minOf { it.y }

fun LassoSelectionState.mapToRectangleSelectionState() : RectangleSelectionState {
    val left = calculateLeftPoint()
    val top = calculateTopPoint()
    val width = calculateWidth()
    val height = calculateHeight()
    val right = left + width
    val bottom = top + height
    val imageOffsetX = left.toInt()
    val imageOffsetY = top.toInt()

    return RectangleSelectionState(
        leftTop = Offset(left, top),
        leftBottom = Offset(left, bottom),
        rightTop = Offset(right, top),
        rightBottom = Offset(right, bottom),
        imageSize = IntSize(width.toInt(), height.toInt()),
        imageOffset = IntOffset(imageOffsetX, imageOffsetY),
        activePoint = null,
    )
}


fun calculateDirection(currentPoint: Point, previousPoint: Point): PointDirection {
    if (currentPoint.x > previousPoint.x) { // move Right
        return when {
            currentPoint.y > previousPoint.y -> PointDirection.DOWN_RIGHT
            currentPoint.y < previousPoint.y -> PointDirection.UP_RIGHT
            else -> PointDirection.RIGHT
        }
    }

    if (currentPoint.x < previousPoint.x) { // move Left
        return when {
            currentPoint.y > previousPoint.y -> PointDirection.DOWN_LEFT
            currentPoint.y < previousPoint.y -> PointDirection.UP_LEFT
            else -> PointDirection.LEFT
        }
    }

    if (currentPoint.y > previousPoint.y) { // move Down
        return when {
            currentPoint.x > previousPoint.x -> PointDirection.DOWN_RIGHT
            currentPoint.x < previousPoint.x -> PointDirection.DOWN_LEFT
            else -> PointDirection.DOWN
        }
    }

    if (currentPoint.y < previousPoint.y) { // move Up
        return when {
            currentPoint.x > previousPoint.x -> PointDirection.UP_RIGHT
            currentPoint.x < previousPoint.x -> PointDirection.UP_LEFT
            else -> PointDirection.UP
        }
    }

    return PointDirection.UNDEFINED
}
