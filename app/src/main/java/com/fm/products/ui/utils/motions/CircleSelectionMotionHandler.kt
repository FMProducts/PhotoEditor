package com.fm.products.ui.utils.motions

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.CircleSelectionPosition
import com.fm.products.ui.models.CircleSelectionPosition.ActivePoint
import com.fm.products.ui.utils.calculateBottomPoint
import com.fm.products.ui.utils.calculateLeftPoint
import com.fm.products.ui.utils.calculateRightPoint
import com.fm.products.ui.utils.calculateTopPoint
import kotlin.math.abs

class CircleSelectionMotionHandler(
    var circleSelectionPosition: CircleSelectionPosition,
    var imagePosition: IntOffset,
    var imageSize: IntSize,
    val onUpdateCircleSelectionPosition: (CircleSelectionPosition) -> Unit,
) : MotionHandler {

    private var isCanMoveOutsideImage: Boolean = true
    private var pressDownPoint: PressDownPoint? = null

    override fun handleMotion(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                circleSelectionHandleActionDown(event.x, event.y)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                circleSelectionHandleActionMove(event.x, event.y)
                true
            }


            MotionEvent.ACTION_UP -> {
                circleSelectionHandleActionUp()
                true
            }

            else -> false
        }
    }

    private fun circleSelectionHandleActionDown(x: Float, y: Float) {
        when {
            // tap on top circle
            isInOffset(x, y, calculateTopPoint(circleSelectionPosition)) -> {
                onUpdateCircleSelectionPosition(
                    circleSelectionPosition.copy(activePoint = ActivePoint.TOP)
                )
            }
            // tap on bottom circle
            isInOffset(x, y, calculateBottomPoint(circleSelectionPosition)) -> {
                onUpdateCircleSelectionPosition(
                    circleSelectionPosition.copy(activePoint = ActivePoint.BOTTOM)
                )
            }
            // tap on right circle
            isInOffset(x, y, calculateRightPoint(circleSelectionPosition)) -> {
                onUpdateCircleSelectionPosition(
                    circleSelectionPosition.copy(activePoint = ActivePoint.RIGHT)
                )
            }
            // tap on left circle
            isInOffset(x, y, calculateLeftPoint(circleSelectionPosition)) -> {
                onUpdateCircleSelectionPosition(
                    circleSelectionPosition.copy(activePoint = ActivePoint.LEFT)
                )
            }
            // tap in circle selection
            isInCircleSelection(x, y) -> {
                pressDownPoint = calculatePressDownPoint(x, y)
                onUpdateCircleSelectionPosition(
                    circleSelectionPosition.copy(activePoint = null)
                )
            }
        }
    }

    private fun circleSelectionHandleActionUp() {
        pressDownPoint = null
        onUpdateCircleSelectionPosition(
            circleSelectionPosition.copy(activePoint = null)
        )
    }

    private fun circleSelectionHandleActionMove(x: Float, y: Float) {

        when (circleSelectionPosition.activePoint) {
            ActivePoint.TOP -> {
                moveVerticalPoint(y)
            }

            ActivePoint.BOTTOM -> {
                moveVerticalPoint(y)
            }

            ActivePoint.LEFT -> {
                moveHorizontalPoint(x)
            }

            ActivePoint.RIGHT -> {
                moveHorizontalPoint(x)
            }

            null -> {
                if (isInCircleSelection(x, y)) moveCirclePositionTo(x, y)
            }
        }
    }

    private fun moveHorizontalPoint(x: Float) {
        val newRadius = abs(circleSelectionPosition.center.x - x)

        val newCircleSelectionPosition = circleSelectionPosition.copy(radius = newRadius)
        if (newRadius >= MIN_RADIUS && checkIsNotMoveOutsize(newCircleSelectionPosition)) {
            onUpdateCircleSelectionPosition(newCircleSelectionPosition)
        }
    }

    private fun moveVerticalPoint(y: Float) {
        val newRadius = abs(circleSelectionPosition.center.y - y)

        val newCircleSelectionPosition = circleSelectionPosition.copy(radius = newRadius)
        if (newRadius >= MIN_RADIUS && checkIsNotMoveOutsize(newCircleSelectionPosition)) {
            onUpdateCircleSelectionPosition(newCircleSelectionPosition)
        }
    }

    private fun moveCirclePositionTo(x: Float, y: Float) {
        val downPoint = pressDownPoint ?: return
        val horizontalDistance = x - circleSelectionPosition.center.x
        val verticalDistance = y - circleSelectionPosition.center.y
        val dragLengthX = (horizontalDistance - downPoint.horizontalDistance).toInt()
        val dragLengthY = (verticalDistance- downPoint.verticalDistance).toInt()
        val newCenterX = circleSelectionPosition.center.x + dragLengthX
        val newCenterY = circleSelectionPosition.center.y + dragLengthY

        if (checkIsNotMoveOutsize(newCenterX, newCenterY)) {
            onUpdateCircleSelectionPosition(
                circleSelectionPosition.copy(
                    center = Offset(newCenterX, newCenterY)
                )
            )
        }
    }

    private fun checkIsNotMoveOutsize(centerX: Float, centerY: Float): Boolean {
        if (isCanMoveOutsideImage) return true

        val rightPoint = centerX + circleSelectionPosition.radius
        if (rightPoint > imagePosition.x + imageSize.width) return false

        val leftPoint = centerX - circleSelectionPosition.radius
        if (leftPoint < imagePosition.x) return false

        val topPoint = centerY - circleSelectionPosition.radius
        if (topPoint < imagePosition.y) return false

        val bottomPoint = centerY + circleSelectionPosition.radius
        if (bottomPoint > imagePosition.y + imageSize.height) return false

        return true
    }

    private fun checkIsNotMoveOutsize(circleSelectionPosition: CircleSelectionPosition): Boolean {
        if (isCanMoveOutsideImage) return true

        val rightPoint = circleSelectionPosition.center.x + circleSelectionPosition.radius
        if (rightPoint > imagePosition.x + imageSize.width) return false

        val leftPoint = circleSelectionPosition.center.x - circleSelectionPosition.radius
        if (leftPoint < imagePosition.x) return false

        val topPoint = circleSelectionPosition.center.y - circleSelectionPosition.radius
        if (topPoint < imagePosition.y) return false

        val bottomPoint = circleSelectionPosition.center.y + circleSelectionPosition.radius
        if (bottomPoint > imagePosition.y + imageSize.height) return false

        return true
    }

    private fun isInOffset(x: Float, y: Float, offset: Offset): Boolean {
        val xStart = offset.x - TOLERANCE
        val xEnd = offset.x + TOLERANCE
        val yStart = offset.y - TOLERANCE
        val yEnd = offset.y + TOLERANCE
        return x in xStart..xEnd && y in yStart..yEnd
    }

    private fun isInCircleSelection(x: Float, y: Float): Boolean {
        val xStart = circleSelectionPosition.center.x - circleSelectionPosition.radius
        val xEnd = circleSelectionPosition.center.x + circleSelectionPosition.radius
        val yStart = circleSelectionPosition.center.y - circleSelectionPosition.radius
        val yEnd = circleSelectionPosition.center.y + circleSelectionPosition.radius
        return x in xStart..xEnd && y in yStart..yEnd
    }

    private fun calculatePressDownPoint(x: Float, y: Float): PressDownPoint {
        return PressDownPoint(
            horizontalDistance = x - circleSelectionPosition.center.x,
            verticalDistance = y - circleSelectionPosition.center.y
        )
    }

    private data class PressDownPoint(
        val horizontalDistance: Float,
        val verticalDistance: Float,
    )

    companion object {
        //  допустимое отклонение
        private const val TOLERANCE = 30
        private const val MIN_RADIUS = 40
    }
}
