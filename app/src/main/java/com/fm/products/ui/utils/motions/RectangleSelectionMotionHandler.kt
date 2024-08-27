package com.fm.products.ui.utils.motions

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.RectangleSelectionState
import com.fm.products.ui.models.RectangleSelectionState.ActivePoint
import com.fm.products.ui.utils.selections.calculateLeftBottomPoint
import com.fm.products.ui.utils.selections.calculateLeftTopPoint
import com.fm.products.ui.utils.selections.calculateRightBottomPoint
import com.fm.products.ui.utils.selections.calculateRightTopPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RectangleSelectionMotionHandler(
    rectangleSelectionState: RectangleSelectionState,
    var imagePosition: IntOffset,
    var imageSize: IntSize,
) : MotionHandler {

    private var isCanMoveOutsideImage: Boolean = true
    private var pressDownPoint: PressDownPoint? = null

    private val _selectionState = MutableStateFlow(rectangleSelectionState)
    override val selectionState = _selectionState.asStateFlow()

    private val rectangleSelectionState: RectangleSelectionState
        get() = _selectionState.value

    override fun handleMotion(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                rectangleSelectionHandleActionDown(event.x, event.y)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                rectangleSelectionHandleActionMove(event.x, event.y)
                true
            }

            MotionEvent.ACTION_UP -> {
                rectangleSelectionHandleActionUp()
                true
            }

            else -> false
        }
    }

    override fun update(
        imageSize: IntSize,
        imagePosition: IntOffset
    ) {
        this.imageSize = imageSize
        this.imagePosition = imagePosition
    }

    private fun rectangleSelectionHandleActionDown(x: Float, y: Float) {

        val recImagePosition = rectangleSelectionState.imageOffset
        val recImageSize = rectangleSelectionState.imageSize
        when {
            // tap on left top Point
            isInOffset(x, y, calculateLeftTopPoint(recImagePosition)) -> {
                _selectionState.update {
                    it.copy(activePoint = ActivePoint.LEFT_TOP)
                }
            }
            // tap on right top Point
            isInOffset(x, y, calculateRightTopPoint(recImagePosition, recImageSize)) -> {
                _selectionState.update {
                    it.copy(activePoint = ActivePoint.RIGHT_TOP)
                }
            }
            // tap on left bottom Point
            isInOffset(x, y, calculateLeftBottomPoint(recImagePosition, recImageSize)) -> {
                _selectionState.update {
                    it.copy(activePoint = ActivePoint.LEFT_BOTTOM)
                }
            }
            // tap on right bottom Point
            isInOffset(x, y, calculateRightBottomPoint(recImagePosition, recImageSize)) -> {
                _selectionState.update {
                    it.copy(activePoint = ActivePoint.RIGHT_BOTTOM)
                }
            }
            // tap in rectangle selection
            isInRectangleSelection(x, y) -> {
                pressDownPoint = calculatePressDownPoint(x, y)
                _selectionState.update {
                    it.copy(activePoint = null)
                }
            }
        }
    }

    private fun rectangleSelectionHandleActionUp() {
        pressDownPoint = null

        _selectionState.update {
            it.copy(activePoint = null)
        }
    }

    private fun rectangleSelectionHandleActionMove(x: Float, y: Float) {
        when (rectangleSelectionState.activePoint) {
            ActivePoint.LEFT_TOP -> {
                moveLeftTop(x, y)
            }

            ActivePoint.RIGHT_TOP -> {
                moveRightTop(x, y)
            }

            ActivePoint.LEFT_BOTTOM -> {
                moveLeftBottom(x, y)
            }

            ActivePoint.RIGHT_BOTTOM -> {
                moveRightBottom(x, y)
            }

            null -> {
                if (isInRectangleSelection(x, y)) moveRectangle(x, y)
            }
        }
    }

    private fun moveRectangle(x: Float, y: Float) {
        val downPoint = pressDownPoint ?: return
        val rightTopPointDistanceX = rectangleSelectionState.rightTop.x - x
        val rightBottomPointDistanceY = rectangleSelectionState.rightBottom.y - y
        val dragLengthX = (downPoint.horizontalDistance - rightTopPointDistanceX).toInt()
        val dragLengthY = (downPoint.verticalDistance - rightBottomPointDistanceY).toInt()

        val newRectangleSelectionPosition = rectangleSelectionState.copy(
            leftTop = rectangleSelectionState.leftTop.copy(
                x = rectangleSelectionState.leftTop.x + dragLengthX,
                y = rectangleSelectionState.leftTop.y + dragLengthY,
            ),
            leftBottom = rectangleSelectionState.leftBottom.copy(
                x = rectangleSelectionState.leftBottom.x + dragLengthX,
                y = rectangleSelectionState.leftBottom.y + dragLengthY,
            ),
            rightTop = rectangleSelectionState.rightTop.copy(
                x = rectangleSelectionState.rightTop.x + dragLengthX,
                y = rectangleSelectionState.rightTop.y + dragLengthY,
            ),
            rightBottom = rectangleSelectionState.rightBottom.copy(
                x = rectangleSelectionState.rightBottom.x + dragLengthX,
                y = rectangleSelectionState.rightBottom.y + dragLengthY,
            ),
            imageOffset = rectangleSelectionState.imageOffset.copy(
                x = rectangleSelectionState.imageOffset.x + dragLengthX,
                y = rectangleSelectionState.imageOffset.y + dragLengthY,
            )
        )

        if (checkIsNotMoveOutside(newRectangleSelectionPosition)) {
            _selectionState.update { newRectangleSelectionPosition }
        }
    }

    private fun moveLeftTop(x: Float, y: Float) {
        val newDrawSize = IntSize(
            width = (rectangleSelectionState.rightTop.x - x).toInt(),
            height = (rectangleSelectionState.rightBottom.y - y).toInt(),
        )
        val newRectangleSelectionPosition = rectangleSelectionState.copy(
            leftTop = rectangleSelectionState.leftTop.copy(x = x, y = y),
            leftBottom = rectangleSelectionState.leftBottom.copy(x = x),
            rightTop = rectangleSelectionState.rightTop.copy(y = y),
            imageOffset = rectangleSelectionState.imageOffset.copy(x = x.toInt(), y = y.toInt()),
            imageSize = newDrawSize
        )

        if (checkIsNotMoveOutside(newRectangleSelectionPosition)) {
            _selectionState.update { newRectangleSelectionPosition }
        }
    }

    private fun moveLeftBottom(x: Float, y: Float) {
        val newDrawSize = IntSize(
            width = (rectangleSelectionState.rightTop.x - x).toInt(),
            height = (y - rectangleSelectionState.leftTop.y).toInt(),
        )
        val newRectangleSelectionPosition = rectangleSelectionState.copy(
            leftBottom = rectangleSelectionState.leftBottom.copy(x = x, y = y),
            leftTop = rectangleSelectionState.leftTop.copy(x = x),
            rightBottom = rectangleSelectionState.rightBottom.copy(y = y),
            imageOffset = rectangleSelectionState.imageOffset.copy(
                x = x.toInt(),
                y = y.toInt() - newDrawSize.height,
            ),
            imageSize = newDrawSize
        )

        if (checkIsNotMoveOutside(newRectangleSelectionPosition)) {
            _selectionState.update { newRectangleSelectionPosition }
        }
    }

    private fun moveRightTop(x: Float, y: Float) {
        val newDrawSize = IntSize(
            width = (x - rectangleSelectionState.leftTop.x).toInt(),
            height = (rectangleSelectionState.rightBottom.y - y).toInt(),
        )

        val newRectangleSelectionPosition = rectangleSelectionState.copy(
            rightTop = rectangleSelectionState.rightTop.copy(x = x, y = y),
            leftTop = rectangleSelectionState.leftTop.copy(y = y),
            rightBottom = rectangleSelectionState.rightBottom.copy(x = x),
            imageOffset = rectangleSelectionState.imageOffset.copy(
                x = x.toInt() - newDrawSize.width,
                y = y.toInt()
            ),
            imageSize = newDrawSize,
        )


        if (checkIsNotMoveOutside(newRectangleSelectionPosition)) {
            _selectionState.update { newRectangleSelectionPosition }
        }
    }

    private fun moveRightBottom(x: Float, y: Float) {
        val newDrawSize = IntSize(
            width = (x - rectangleSelectionState.leftTop.x).toInt(),
            height = (y - rectangleSelectionState.rightTop.y).toInt(),
        )
        val newRectangleSelectionPosition = rectangleSelectionState.copy(
            rightBottom = rectangleSelectionState.rightBottom.copy(x = x, y = y),
            rightTop = rectangleSelectionState.rightTop.copy(x = x),
            leftBottom = rectangleSelectionState.leftBottom.copy(y = y),
            imageOffset = rectangleSelectionState.imageOffset.copy(
                x = x.toInt() - newDrawSize.width,
                y = y.toInt() - newDrawSize.height,
            ),
            imageSize = newDrawSize
        )

        if (checkIsNotMoveOutside(newRectangleSelectionPosition)) {
            _selectionState.update { newRectangleSelectionPosition }
        }
    }

    private fun checkIsNotMoveOutside(rectangleSelectionState: RectangleSelectionState): Boolean {
        if (isCanMoveOutsideImage) return true

        val leftTopY = rectangleSelectionState.leftTop.y
        val rightTopY = rectangleSelectionState.rightTop.y
        if (leftTopY < imagePosition.y || rightTopY < imagePosition.y) return false

        val leftTopX = rectangleSelectionState.leftTop.x
        val leftBottomX = rectangleSelectionState.leftBottom.x
        if (leftTopX < imagePosition.x || leftBottomX < imagePosition.x) return false

        val leftBottomY = rectangleSelectionState.leftBottom.y
        val rightBottomY = rectangleSelectionState.rightBottom.y
        val imageBottomPoint = imagePosition.y + imageSize.height
        if (leftBottomY > imageBottomPoint || rightBottomY > imageBottomPoint) return false

        val rightTopX = rectangleSelectionState.rightTop.x
        val rightBottomX = rectangleSelectionState.rightBottom.x
        val imageRightPoint = imagePosition.x + imageSize.width
        if (rightTopX > imageRightPoint || rightBottomX > imageBottomPoint) return false

        return true
    }

    private fun isInRectangleSelection(x: Float, y: Float): Boolean {
        val xStart = rectangleSelectionState.leftTop.x
        val xEnd = rectangleSelectionState.rightTop.x
        val yStart = rectangleSelectionState.leftTop.y
        val yEnd = rectangleSelectionState.leftBottom.y
        return x in xStart..xEnd && y in yStart..yEnd
    }

    private fun isInOffset(x: Float, y: Float, offset: Offset): Boolean {
        val xStart = offset.x - TOLERANCE
        val xEnd = offset.x + TOLERANCE
        val yStart = offset.y - TOLERANCE
        val yEnd = offset.y + TOLERANCE
        return x in xStart..xEnd && y in yStart..yEnd
    }

    private fun calculatePressDownPoint(x: Float, y: Float): PressDownPoint {
        return PressDownPoint(
            horizontalDistance = rectangleSelectionState.rightTop.x - x,
            verticalDistance = rectangleSelectionState.rightBottom.y - y
        )
    }

    private data class PressDownPoint(
        val horizontalDistance: Float,
        val verticalDistance: Float,
    )

    companion object {
        //  допустимое отклонение
        private const val TOLERANCE = 40
    }
}
