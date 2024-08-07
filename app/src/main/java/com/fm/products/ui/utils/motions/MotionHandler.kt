package com.fm.products.ui.utils.motions

import android.view.MotionEvent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fm.products.ui.models.SelectionState
import kotlinx.coroutines.flow.StateFlow

interface MotionHandler {

    val selectionState: StateFlow<SelectionState>

    fun handleMotion(event: MotionEvent): Boolean

    fun update(imageSize: IntSize, imagePosition: IntOffset)
}
