package com.fm.products.ui.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.CircleSelectionPosition
import com.fm.products.ui.models.CircleSelectionPosition.ActivePoint
import kotlin.math.min


fun calculateDefaultCircleSelectionPosition(
    drawSize: IntSize,
    center: Offset,
) = CircleSelectionPosition(
    center = center,
    radius = min(
        (drawSize.height.toFloat() / 2) - 20,
        (drawSize.width.toFloat() / 2) - 20
    ),
    activePoint = null,
)

fun calculateLeftPoint(circleSelectionPosition: CircleSelectionPosition) = Offset(
    x = circleSelectionPosition.center.x - circleSelectionPosition.radius,
    y = circleSelectionPosition.center.y,
)

fun calculateRightPoint(circleSelectionPosition: CircleSelectionPosition) = Offset(
    x = circleSelectionPosition.center.x + circleSelectionPosition.radius,
    y = circleSelectionPosition.center.y,
)

fun calculateTopPoint(circleSelectionPosition: CircleSelectionPosition) = Offset(
    x = circleSelectionPosition.center.x,
    y = circleSelectionPosition.center.y - circleSelectionPosition.radius,
)

fun calculateBottomPoint(circleSelectionPosition: CircleSelectionPosition) = Offset(
    x = circleSelectionPosition.center.x,
    y = circleSelectionPosition.center.y + circleSelectionPosition.radius,
)

fun DrawScope.drawCircleSelection(position: CircleSelectionPosition) {
    drawCircle(
        color = Color.LightGray,
        radius = position.radius,
        style = dashStyle(),
        center = position.center,
    )

    drawCircle(
        color = pointColor(isActive = position.activePoint == ActivePoint.LEFT),
        radius = 20f,
        center = calculateLeftPoint(position),
    )

    drawCircle(
        color = pointColor(isActive = position.activePoint == ActivePoint.RIGHT),
        radius = 20f,
        center = calculateRightPoint(position),
    )

    drawCircle(
        color = pointColor(isActive = position.activePoint == ActivePoint.TOP),
        radius = 20f,
        center = calculateTopPoint(position),
    )

    drawCircle(
        color = pointColor(isActive = position.activePoint == ActivePoint.BOTTOM),
        radius = 20f,
        center = calculateBottomPoint(position),
    )
}
