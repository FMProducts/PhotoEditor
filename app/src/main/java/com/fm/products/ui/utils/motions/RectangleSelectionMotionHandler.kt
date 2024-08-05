package com.fm.products.ui.utils.motions

import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.RectangleSelectionPosition
import com.fm.products.ui.models.RectangleSelectionPosition.ActivePoint
import com.fm.products.ui.utils.calculateLeftBottomPoint
import com.fm.products.ui.utils.calculateLeftTopPoint
import com.fm.products.ui.utils.calculateRightBottomPoint
import com.fm.products.ui.utils.calculateRightTopPoint

class RectangleSelectionMotionHandler(
    var rectangleSelectionPosition: RectangleSelectionPosition,
    val onUpdateRectangleSelectionPosition: (RectangleSelectionPosition) -> Unit,
    var imagePosition: IntOffset,
    var imageSize: IntSize,
) : MotionHandler {

    private var isCanMoveOutsideImage: Boolean = true
    private var pressDownPoint: PressDownPoint? = null

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

    private fun rectangleSelectionHandleActionDown(x: Float, y: Float) {

        val recImagePosition = rectangleSelectionPosition.drawOffset
        val recImageSize = rectangleSelectionPosition.drawSize
        when {
            // tap on left top Point
            isInOffset(x, y, calculateLeftTopPoint(recImagePosition)) -> {
                onUpdateRectangleSelectionPosition(
                    rectangleSelectionPosition.copy(activePoint = ActivePoint.LEFT_TOP)
                )
            }
            // tap on right top Point
            isInOffset(x, y, calculateRightTopPoint(recImagePosition, recImageSize)) -> {
                onUpdateRectangleSelectionPosition(
                    rectangleSelectionPosition.copy(activePoint = ActivePoint.RIGHT_TOP)
                )
            }
            // tap on left bottom Point
            isInOffset(x, y, calculateLeftBottomPoint(recImagePosition, recImageSize)) -> {
                onUpdateRectangleSelectionPosition(
                    rectangleSelectionPosition.copy(activePoint = ActivePoint.LEFT_BOTTOM)
                )
            }
            // tap on right bottom Point
            isInOffset(x, y, calculateRightBottomPoint(recImagePosition, recImageSize)) -> {
                onUpdateRectangleSelectionPosition(
                    rectangleSelectionPosition.copy(activePoint = ActivePoint.RIGHT_BOTTOM)
                )
            }
            // tap in rectangle selection
            isInRectangleSelection(x, y) -> {
                pressDownPoint = calculatePressDownPoint(x, y)
                onUpdateRectangleSelectionPosition(
                    rectangleSelectionPosition.copy(activePoint = null)
                )
            }
        }
    }

    private fun rectangleSelectionHandleActionUp() {
        pressDownPoint = null

        onUpdateRectangleSelectionPosition(
            rectangleSelectionPosition.copy(activePoint = null)
        )
    }

    private fun rectangleSelectionHandleActionMove(x: Float, y: Float) {
        when (rectangleSelectionPosition.activePoint) {
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
        val rightTopPointDistanceX = rectangleSelectionPosition.rightTop.x - x
        val rightBottomPointDistanceY = rectangleSelectionPosition.rightBottom.y - y
        val dragLengthX = (downPoint.horizontalDistance - rightTopPointDistanceX).toInt()
        val dragLengthY = (downPoint.verticalDistance - rightBottomPointDistanceY).toInt()

        val newRectangleSelectionPosition = rectangleSelectionPosition.copy(
            leftTop = rectangleSelectionPosition.leftTop.copy(
                x = rectangleSelectionPosition.leftTop.x + dragLengthX,
                y = rectangleSelectionPosition.leftTop.y + dragLengthY,
            ),
            leftBottom = rectangleSelectionPosition.leftBottom.copy(
                x = rectangleSelectionPosition.leftBottom.x + dragLengthX,
                y = rectangleSelectionPosition.leftBottom.y + dragLengthY,
            ),
            rightTop = rectangleSelectionPosition.rightTop.copy(
                x = rectangleSelectionPosition.rightTop.x + dragLengthX,
                y = rectangleSelectionPosition.rightTop.y + dragLengthY,
            ),
            rightBottom = rectangleSelectionPosition.rightBottom.copy(
                x = rectangleSelectionPosition.rightBottom.x + dragLengthX,
                y = rectangleSelectionPosition.rightBottom.y + dragLengthY,
            ),
            drawOffset = rectangleSelectionPosition.drawOffset.copy(
                x = rectangleSelectionPosition.drawOffset.x + dragLengthX,
                y = rectangleSelectionPosition.drawOffset.y + dragLengthY,
            )
        )

        if (checkIsNotMoveOutside(newRectangleSelectionPosition)) {
            onUpdateRectangleSelectionPosition(newRectangleSelectionPosition)
        }
    }

    private fun moveLeftTop(x: Float, y: Float) {
        val newDrawSize = IntSize(
            width = (rectangleSelectionPosition.rightTop.x - x).toInt(),
            height = (rectangleSelectionPosition.rightBottom.y - y).toInt(),
        )
        val newRectangleSelectionPosition = rectangleSelectionPosition.copy(
            leftTop = rectangleSelectionPosition.leftTop.copy(x = x, y = y),
            leftBottom = rectangleSelectionPosition.leftBottom.copy(x = x),
            rightTop = rectangleSelectionPosition.rightTop.copy(y = y),
            drawOffset = rectangleSelectionPosition.drawOffset.copy(x = x.toInt(), y = y.toInt()),
            drawSize = newDrawSize
        )

        if (checkIsNotMoveOutside(newRectangleSelectionPosition)) {
            onUpdateRectangleSelectionPosition(newRectangleSelectionPosition)
        }
    }

    private fun moveLeftBottom(x: Float, y: Float) {
        val newDrawSize = IntSize(
            width = (rectangleSelectionPosition.rightTop.x - x).toInt(),
            height = (y - rectangleSelectionPosition.leftTop.y).toInt(),
        )
        val newRectangleSelectionPosition = rectangleSelectionPosition.copy(
            leftBottom = rectangleSelectionPosition.leftBottom.copy(x = x, y = y),
            leftTop = rectangleSelectionPosition.leftTop.copy(x = x),
            rightBottom = rectangleSelectionPosition.rightBottom.copy(y = y),
            drawOffset = rectangleSelectionPosition.drawOffset.copy(
                x = x.toInt(),
                y = y.toInt() - newDrawSize.height,
            ),
            drawSize = newDrawSize
        )

        if (checkIsNotMoveOutside(newRectangleSelectionPosition)) {
            onUpdateRectangleSelectionPosition(newRectangleSelectionPosition)
        }
    }

    private fun moveRightTop(x: Float, y: Float) {
        val newDrawSize = IntSize(
            width = (x - rectangleSelectionPosition.leftTop.x).toInt(),
            height = (rectangleSelectionPosition.rightBottom.y - y).toInt(),
        )

        val newRectangleSelectionPosition = rectangleSelectionPosition.copy(
            rightTop = rectangleSelectionPosition.rightTop.copy(x = x, y = y),
            leftTop = rectangleSelectionPosition.leftTop.copy(y = y),
            rightBottom = rectangleSelectionPosition.rightBottom.copy(x = x),
            drawOffset = rectangleSelectionPosition.drawOffset.copy(
                x = x.toInt() - newDrawSize.width,
                y = y.toInt()
            ),
            drawSize = newDrawSize,
        )


        if (checkIsNotMoveOutside(newRectangleSelectionPosition)) {
            onUpdateRectangleSelectionPosition(newRectangleSelectionPosition)
        }
    }

    private fun moveRightBottom(x: Float, y: Float) {
        val newDrawSize = IntSize(
            width = (x - rectangleSelectionPosition.leftTop.x).toInt(),
            height = (y - rectangleSelectionPosition.rightTop.y).toInt(),
        )
        val newRectangleSelectionPosition = rectangleSelectionPosition.copy(
            rightBottom = rectangleSelectionPosition.rightBottom.copy(x = x, y = y),
            rightTop = rectangleSelectionPosition.rightTop.copy(x = x),
            leftBottom = rectangleSelectionPosition.leftBottom.copy(y = y),
            drawOffset = rectangleSelectionPosition.drawOffset.copy(
                x = x.toInt() - newDrawSize.width,
                y = y.toInt() - newDrawSize.height,
            ),
            drawSize = newDrawSize
        )

        if (checkIsNotMoveOutside(newRectangleSelectionPosition)) {
            onUpdateRectangleSelectionPosition(newRectangleSelectionPosition)
        }
    }

    private fun checkIsNotMoveOutside(rectangleSelectionPosition: RectangleSelectionPosition): Boolean {
        if (isCanMoveOutsideImage) return true

        val leftTopY = rectangleSelectionPosition.leftTop.y
        val rightTopY = rectangleSelectionPosition.rightTop.y
        if (leftTopY < imagePosition.y || rightTopY < imagePosition.y) return false

        val leftTopX = rectangleSelectionPosition.leftTop.x
        val leftBottomX = rectangleSelectionPosition.leftBottom.x
        if (leftTopX < imagePosition.x || leftBottomX < imagePosition.x) return false

        val leftBottomY = rectangleSelectionPosition.leftBottom.y
        val rightBottomY = rectangleSelectionPosition.rightBottom.y
        val imageBottomPoint = imagePosition.y + imageSize.height
        if (leftBottomY > imageBottomPoint || rightBottomY > imageBottomPoint) return false

        val rightTopX = rectangleSelectionPosition.rightTop.x
        val rightBottomX = rectangleSelectionPosition.rightBottom.x
        val imageRightPoint = imagePosition.x + imageSize.width
        if (rightTopX > imageRightPoint || rightBottomX > imageBottomPoint) return false

        return true
    }

    private fun isInRectangleSelection(x: Float, y: Float): Boolean {
        val xStart = rectangleSelectionPosition.leftTop.x
        val xEnd = rectangleSelectionPosition.rightTop.x
        val yStart = rectangleSelectionPosition.leftTop.y
        val yEnd = rectangleSelectionPosition.leftBottom.y
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
            horizontalDistance = rectangleSelectionPosition.rightTop.x - x,
            verticalDistance = rectangleSelectionPosition.rightBottom.y - y
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
