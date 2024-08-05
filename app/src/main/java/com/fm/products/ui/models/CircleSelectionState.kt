package com.fm.products.ui.models

import androidx.compose.ui.geometry.Offset
import com.fm.products.ui.utils.emptyOffset
import com.fm.products.ui.utils.isEmpty

data class CircleSelectionState(
    val center: Offset,
    val radius: Float,
    val activePoint: ActivePoint?,
) : SelectionState {

    constructor() : this(center = emptyOffset(), radius = 0f, activePoint = null)

    override fun isEmpty() = center.isEmpty() || radius == 0f

    enum class ActivePoint {
        TOP, BOTTOM, LEFT, RIGHT,
    }
}
