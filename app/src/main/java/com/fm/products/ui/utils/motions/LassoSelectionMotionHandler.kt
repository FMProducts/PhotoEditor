package com.fm.products.ui.utils.motions

import android.graphics.RectF
import android.view.MotionEvent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.LassoSelectionState
import com.fm.products.ui.models.LassoSelectionState.Point
import com.fm.products.ui.models.LassoSelectionState.PointDirection
import com.fm.products.ui.utils.calculateDirection
import com.fm.products.ui.utils.calculateDistanceBetweenPoints
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

class LassoSelectionMotionHandler(
    lassoSelectionState: LassoSelectionState,
    var imagePosition: IntOffset,
    var imageSize: IntSize,
) : MotionHandler {

    private var pressDownPoint: PressDownPoint? = null

    private val _selectionState = MutableStateFlow(lassoSelectionState)
    override val selectionState = _selectionState.asStateFlow()

    private val lassoSelectionState: LassoSelectionState
        get() = _selectionState.value

    override fun handleMotion(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lassoSelectionHandleActionDown(event.x, event.y)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                lassoSelectionHandleActionMove(event.x, event.y)
                true
            }

            MotionEvent.ACTION_UP -> {
                lassoSelectionHandleActionUp(event.x, event.y)
                true
            }

            else -> false
        }
    }

    override fun update(imageSize: IntSize, imagePosition: IntOffset) {
        this.imageSize = imageSize
        this.imagePosition = imagePosition
    }

    private fun lassoSelectionHandleActionDown(x: Float, y: Float) {
        when {
            checkInSelection(x, y) -> moveActionDown(x, y)
            else -> drawActionDown(x, y)
        }
    }

    private fun moveActionDown(x: Float, y: Float) {
        pressDownPoint = calculatePressDownPoint(x, y)

        _selectionState.update {
            it.copy(isMove = true)
        }
    }

    private fun drawActionDown(x: Float, y: Float) {
        val firstPoint = Point(x, y, PointDirection.UNDEFINED)

        _selectionState.update {
            it.copy(
                points = listOf(firstPoint),
                isDraw = true
            )
        }
    }

    private fun lassoSelectionHandleActionMove(x: Float, y: Float) {
        when {
            lassoSelectionState.isMove -> moveActionMove(x, y)
            else -> drawActionMove(x, y)
        }
    }

    private fun moveActionMove(x: Float, y: Float) {
        val downPoint = pressDownPoint ?: return
        val firstPoint = lassoSelectionState.points.firstOrNull() ?: return
        val horizontalDistance = firstPoint.x - x
        val verticalDistance = firstPoint.y - y
        val dragLengthX = (downPoint.horizontalDistance - horizontalDistance).toInt()
        val drawLengthY = (downPoint.verticalDistance - verticalDistance).toInt()

        val points = lassoSelectionState.points.map {
            it.copy(x = it.x + dragLengthX, y = it.y + drawLengthY)
        }
        _selectionState.update {
            it.copy(
                points = points,
                isMove = true,
            )
        }
    }

    private fun drawActionMove(x: Float, y: Float) {
        val points = lassoSelectionState.points.toMutableList()
        val prevPoint = points.lastOrNull() ?: return

        if (prevPoint.isLastPoint) return

        val newPointDirection = calculateDirection(x, y, prevPoint)
        val isLastPoint = isLastPoint(x, y)
        val newPoint = Point(x, y, newPointDirection, isLastPoint)
        points.add(newPoint)

        if (checkMinDistance(prevPoint, newPoint)) {
            _selectionState.update {
                it.copy(
                    points = points,
                    isDraw = true,
                )
            }
        }
    }

    private fun lassoSelectionHandleActionUp(x: Float, y: Float) {
        when {
            lassoSelectionState.isMove -> moveActionUp()
            else -> drawActionUp(x, y)
        }
    }

    private fun moveActionUp() {
        pressDownPoint = null
        _selectionState.update {
            it.copy(isMove = false)
        }
    }

    private fun drawActionUp(x: Float, y: Float) {
        val points = lassoSelectionState.points.toMutableList()

        if (points.size < MIN_POINTS) {
            _selectionState.update {
                it.copy(points = emptyList(), isDraw = false)
            }
            return
        }

        val prevPoint = points.lastOrNull() ?: return

        if (prevPoint.isLastPoint.not()) {
            val newPointDirection = calculateDirection(x, y, prevPoint)
            val lastPoint = Point(x, y, newPointDirection)
            points.add(lastPoint)
        }

        _selectionState.update {
            it.copy(points = points, isDraw = false)
        }
    }

    private fun calculateDirection(
        currentPointX: Float,
        currentPointY: Float,
        previousPoint: Point,
    ): PointDirection {
        if (lassoSelectionState.isDrawVisualPoints.not()) {
            return PointDirection.UNDEFINED
        }

        return calculateDirection(
            currentPoint = Point(currentPointX, currentPointY, PointDirection.UNDEFINED),
            previousPoint = previousPoint,
        )
    }

    private fun checkMinDistance(previousPoint: Point, newPoint: Point): Boolean {
        val xDiff = abs(previousPoint.x - newPoint.x)
        val yDiff = abs(previousPoint.y - newPoint.y)
        return !(xDiff < MIN_DISTANCE || yDiff < MIN_DISTANCE)
    }

    private fun checkInSelection(x: Float, y: Float): Boolean {
        val points = lassoSelectionState.points
        if (points.isEmpty()) return false

        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }

        return RectF(minX, minY, maxX, maxY).contains(x, y)
    }

    private fun calculatePressDownPoint(x: Float, y: Float): PressDownPoint? {
        val firstPoint = lassoSelectionState.points.firstOrNull() ?: return null
        return PressDownPoint(
            horizontalDistance = firstPoint.x - x,
            verticalDistance = firstPoint.y - y,
        )
    }

    private fun isLastPoint(x: Float, y: Float): Boolean {
        if (lassoSelectionState.points.size < 10) return false

        val firstPoint = lassoSelectionState.points.firstOrNull() ?: return false
        val distance = calculateDistanceBetweenPoints(x, y, firstPoint.x, firstPoint.y)
        return if (distance < LAST_POINT_MIN_DISTANCE) true else false
    }


    private data class PressDownPoint(
        val horizontalDistance: Float,
        val verticalDistance: Float,
    )

    companion object {
        private const val MIN_DISTANCE = 10
        private const val LAST_POINT_MIN_DISTANCE = 80
        private const val MIN_POINTS = 2
    }
}
