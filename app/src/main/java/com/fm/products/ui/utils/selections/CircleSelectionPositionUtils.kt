package com.fm.products.ui.utils.selections

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.CircleSelectionState
import com.fm.products.ui.models.CircleSelectionState.ActivePoint
import com.fm.products.ui.utils.dashStyle
import com.fm.products.ui.utils.pointColor
import kotlin.math.min


fun calculateDefaultCircleSelectionPosition(
    drawSize: IntSize,
    center: Offset,
) = CircleSelectionState(
    center = center,
    radius = min(
        (drawSize.height.toFloat() / 2) - 20,
        (drawSize.width.toFloat() / 2) - 20
    ),
    activePoint = null,
)

fun calculateLeftPoint(circleSelectionState: CircleSelectionState) = Offset(
    x = circleSelectionState.center.x - circleSelectionState.radius,
    y = circleSelectionState.center.y,
)

fun calculateRightPoint(circleSelectionState: CircleSelectionState) = Offset(
    x = circleSelectionState.center.x + circleSelectionState.radius,
    y = circleSelectionState.center.y,
)

fun calculateTopPoint(circleSelectionState: CircleSelectionState) = Offset(
    x = circleSelectionState.center.x,
    y = circleSelectionState.center.y - circleSelectionState.radius,
)

fun calculateBottomPoint(circleSelectionState: CircleSelectionState) = Offset(
    x = circleSelectionState.center.x,
    y = circleSelectionState.center.y + circleSelectionState.radius,
)

fun DrawScope.drawCircleSelection(position: CircleSelectionState) {
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
