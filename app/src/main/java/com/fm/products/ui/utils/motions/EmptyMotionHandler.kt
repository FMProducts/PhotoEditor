package com.fm.products.ui.utils.motions

import android.view.MotionEvent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.SelectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EmptyMotionHandler : MotionHandler {

    override val selectionState: StateFlow<SelectionState> = MutableStateFlow(EmptySelectionState())

    override fun handleMotion(event: MotionEvent): Boolean {
        /* no-op */
        return false
    }

    override fun update(imageSize: IntSize, imagePosition: IntOffset) {
        /* no-op */
    }

    private class EmptySelectionState : SelectionState {
        override fun isEmpty(): Boolean = true
    }
}
