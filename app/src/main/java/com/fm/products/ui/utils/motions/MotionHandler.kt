package com.fm.products.ui.utils.motions

import android.view.MotionEvent

interface MotionHandler {

    fun handleMotion(event: MotionEvent): Boolean
}
