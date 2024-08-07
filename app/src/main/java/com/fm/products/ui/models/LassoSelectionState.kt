package com.fm.products.ui.models


data class LassoSelectionState(
    val points: List<Point>,
    val isDraw: Boolean,
    val isMove: Boolean,
) : SelectionState {

    val isDrawVisualPoints: Boolean = false

    constructor() : this(emptyList(), false, false)

    override fun isEmpty(): Boolean = points.isEmpty()

    data class Point(
        val x: Float,
        val y: Float,
        val direction: PointDirection, // direction relative to previous point
    )

    enum class PointDirection {
        UP, DOWN, RIGHT, LEFT,
        UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT,
        UNDEFINED,
    }
}
