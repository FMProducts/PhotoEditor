package com.fm.products.ui.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.utils.emptyIntOffset
import com.fm.products.ui.utils.emptyIntSize
import com.fm.products.ui.utils.emptyOffset
import com.fm.products.ui.utils.isEmpty

data class RectangleSelectionPosition(
    val leftTop: Offset,
    val leftBottom: Offset,
    val rightTop: Offset,
    val rightBottom: Offset,
    val drawSize: IntSize,
    val drawOffset: IntOffset,
    val activePoint: ActivePoint?,
) {

    constructor() : this(
        leftTop = emptyOffset(),
        leftBottom = emptyOffset(),
        rightTop = emptyOffset(),
        rightBottom = emptyOffset(),
        drawSize = emptyIntSize(),
        drawOffset = emptyIntOffset(),
        activePoint = null,
    )

    fun isEmpty() =
        leftTop.isEmpty() && leftBottom.isEmpty() && rightTop.isEmpty() && rightBottom.isEmpty()

    enum class ActivePoint {
        LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM,
    }
}
