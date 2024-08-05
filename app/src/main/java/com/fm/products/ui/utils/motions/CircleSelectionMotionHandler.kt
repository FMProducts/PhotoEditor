package com.fm.products.ui.utils.motions

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.CircleSelectionState
import com.fm.products.ui.models.CircleSelectionState.ActivePoint
import com.fm.products.ui.utils.calculateBottomPoint
import com.fm.products.ui.utils.calculateLeftPoint
import com.fm.products.ui.utils.calculateRightPoint
import com.fm.products.ui.utils.calculateTopPoint
import kotlin.math.abs

class CircleSelectionMotionHandler(
    var circleSelectionState: CircleSelectionState,
    var imagePosition: IntOffset,
    var imageSize: IntSize,
    val onUpdateCircleSelectionState: (CircleSelectionState) -> Unit,
) : MotionHandler<CircleSelectionState> {

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

    override fun update(state: CircleSelectionState, imageSize: IntSize, imagePosition: IntOffset) {
        this.imageSize = imageSize
        this.imagePosition = imagePosition
        this.circleSelectionState = state
    }

    private fun circleSelectionHandleActionDown(x: Float, y: Float) {
        when {
            // tap on top circle
            isInOffset(x, y, calculateTopPoint(circleSelectionState)) -> {
                onUpdateCircleSelectionState(
                    circleSelectionState.copy(activePoint = ActivePoint.TOP)
                )
            }
            // tap on bottom circle
            isInOffset(x, y, calculateBottomPoint(circleSelectionState)) -> {
                onUpdateCircleSelectionState(
                    circleSelectionState.copy(activePoint = ActivePoint.BOTTOM)
                )
            }
            // tap on right circle
            isInOffset(x, y, calculateRightPoint(circleSelectionState)) -> {
                onUpdateCircleSelectionState(
                    circleSelectionState.copy(activePoint = ActivePoint.RIGHT)
                )
            }
            // tap on left circle
            isInOffset(x, y, calculateLeftPoint(circleSelectionState)) -> {
                onUpdateCircleSelectionState(
                    circleSelectionState.copy(activePoint = ActivePoint.LEFT)
                )
            }
            // tap in circle selection
            isInCircleSelection(x, y) -> {
                pressDownPoint = calculatePressDownPoint(x, y)
                onUpdateCircleSelectionState(
                    circleSelectionState.copy(activePoint = null)
                )
            }
        }
    }

    private fun circleSelectionHandleActionUp() {
        pressDownPoint = null
        onUpdateCircleSelectionState(
            circleSelectionState.copy(activePoint = null)
        )
    }

    private fun circleSelectionHandleActionMove(x: Float, y: Float) {

        when (circleSelectionState.activePoint) {
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
        val newRadius = abs(circleSelectionState.center.x - x)

        val newCircleSelectionPosition = circleSelectionState.copy(radius = newRadius)
        if (newRadius >= MIN_RADIUS && checkIsNotMoveOutside(newCircleSelectionPosition)) {
            onUpdateCircleSelectionState(newCircleSelectionPosition)
        }
    }

    private fun moveVerticalPoint(y: Float) {
        val newRadius = abs(circleSelectionState.center.y - y)

        val newCircleSelectionPosition = circleSelectionState.copy(radius = newRadius)
        if (newRadius >= MIN_RADIUS && checkIsNotMoveOutside(newCircleSelectionPosition)) {
            onUpdateCircleSelectionState(newCircleSelectionPosition)
        }
    }

    private fun moveCirclePositionTo(x: Float, y: Float) {
        val downPoint = pressDownPoint ?: return
        val horizontalDistance = x - circleSelectionState.center.x
        val verticalDistance = y - circleSelectionState.center.y
        val dragLengthX = (horizontalDistance - downPoint.horizontalDistance).toInt()
        val dragLengthY = (verticalDistance- downPoint.verticalDistance).toInt()
        val newCenterX = circleSelectionState.center.x + dragLengthX
        val newCenterY = circleSelectionState.center.y + dragLengthY

        if (checkIsNotMoveOutside(newCenterX, newCenterY)) {
            onUpdateCircleSelectionState(
                circleSelectionState.copy(
                    center = Offset(newCenterX, newCenterY)
                )
            )
        }
    }

    private fun checkIsNotMoveOutside(centerX: Float, centerY: Float): Boolean {
        if (isCanMoveOutsideImage) return true

        val rightPoint = centerX + circleSelectionState.radius
        if (rightPoint > imagePosition.x + imageSize.width) return false

        val leftPoint = centerX - circleSelectionState.radius
        if (leftPoint < imagePosition.x) return false

        val topPoint = centerY - circleSelectionState.radius
        if (topPoint < imagePosition.y) return false

        val bottomPoint = centerY + circleSelectionState.radius
        if (bottomPoint > imagePosition.y + imageSize.height) return false

        return true
    }

    private fun checkIsNotMoveOutside(circleSelectionState: CircleSelectionState): Boolean {
        if (isCanMoveOutsideImage) return true

        val rightPoint = circleSelectionState.center.x + circleSelectionState.radius
        if (rightPoint > imagePosition.x + imageSize.width) return false

        val leftPoint = circleSelectionState.center.x - circleSelectionState.radius
        if (leftPoint < imagePosition.x) return false

        val topPoint = circleSelectionState.center.y - circleSelectionState.radius
        if (topPoint < imagePosition.y) return false

        val bottomPoint = circleSelectionState.center.y + circleSelectionState.radius
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
        val xStart = circleSelectionState.center.x - circleSelectionState.radius
        val xEnd = circleSelectionState.center.x + circleSelectionState.radius
        val yStart = circleSelectionState.center.y - circleSelectionState.radius
        val yEnd = circleSelectionState.center.y + circleSelectionState.radius
        return x in xStart..xEnd && y in yStart..yEnd
    }

    private fun calculatePressDownPoint(x: Float, y: Float): PressDownPoint {
        return PressDownPoint(
            horizontalDistance = x - circleSelectionState.center.x,
            verticalDistance = y - circleSelectionState.center.y
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
