package com.fm.products.ui.utils.motions

import android.view.MotionEvent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.SelectionState

interface MotionHandler<State: SelectionState> {

    fun handleMotion(event: MotionEvent): Boolean

    fun update(state: State, imageSize: IntSize, imagePosition: IntOffset)
}
